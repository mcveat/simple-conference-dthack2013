package client

import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.EvernoteApi.Sandbox
import util.Config
import org.scribe.model.{Verifier, Token}
import models.Conference
import com.evernote.thrift.transport.THttpClient
import com.evernote.thrift.protocol.TBinaryProtocol
import com.evernote.edam.`type`.Note
import com.evernote.edam.notestore.NoteStore.Client

/**
 * User: mcveat
 */
object Evernote {
  val url = "https://sandbox.evernote.com"
  val authUrl = "%s/OAuth.action" format url
  def oauthTokenUrl(key: String) = "%s?oauth_token=%s".format(authUrl, key)
  val config = Config.Evernote
  val service = new ServiceBuilder().provider(classOf[Sandbox]).apiKey(config.key).apiSecret(config.secret)
    .callback(config.callback).build()
  def getRequestToken = {
    val t = service.getRequestToken
    (t.getToken, t.getSecret)
  }
  def getAccessToken(requestToken: String, requestSecret: String, verifier: String) = {
    val t = new Token(requestToken, requestSecret)
    val v = new Verifier(verifier)
    val authToken = new EvernoteAuthToken(service.getAccessToken(t, v))
    (authToken.getToken, authToken.getNoteStoreUrl, authToken.getUserShard)
  }
  def storeAgenda(noteUrl: String, authToken: String, userShard: String, c: Conference) = {
    val note = buildAgendaNote(c)
    val protocol = new TBinaryProtocol(new THttpClient(noteUrl))
    val client = new Client(protocol)
    val newNote = client.createNote(authToken, note)
    val shareKey = client.shareNote(authToken, newNote.getGuid)
    getSharedNoteUrl(userShard, newNote.getGuid, shareKey)
  }
  private def getSharedNoteUrl(shardId: String, guid: String, share: String) =
    "%s/shard/%s/sh/%s/%s".format(url, shardId, guid, share)
  private def buildAgendaNote(c: Conference): Note = {
    val note = new Note
    note.setTitle("Conference call at %s" format c.date)
    val contactsList = c.contacts.map { contact =>
      val number = "<b>%s</b> " format contact.number
      val email = contact.email.map("<i>%s</i>" format _).getOrElse("")
      "<li>%s%s</li>".format(number, email)
    }.mkString
    note.setContent(
      """<?xml version="1.0" encoding="UTF-8"?>
        | <!DOCTYPE en-note SYSTEM "http://xml.evernote.com/pub/enml2.dtd">
        | <en-note>
        |   <h1>You're invited to conference call "%s"</h1>
        |   Conference is scheduled at %s.
        |   <h2>Agenda</h2>
        |   <div>%s</div>
        |   <h2>Participants</h2>
        |   <div>%s</div>
        | </en-note>
      """.stripMargin.format(c.title, c.date, c.agenda, contactsList))
    note
  }
}
