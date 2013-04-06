package client

import models.{Contact, Conference}
import util.Config
import com.telekom.api.common.auth.TelekomOAuth2Auth
import com.telekom.api.conferencecall.ConferenceCallClient
import com.telekom.api.conferencecall.model.{NewParticipantRequest, CreateConferenceRequest}

/**
 * User: mcveat
 */
object DevGarden {
  lazy val config = Config.DevGarden
  def startConference(c: Conference) {
    val participants = c.contacts.filter(_.number.isDefined)
    if (participants.isEmpty) return
    val auth = new TelekomOAuth2Auth(config.clientId, config.secret, config.scope)
    auth.requestAccessToken()
    if (!auth.hasValidToken) throw new RuntimeException("invalid devgarden token")
    val client = new ConferenceCallClient(auth, config.env)
    val call = new ConferenceCall(auth, client, c.title)
    call.setParticipants(participants)
    call.start
  }

  private class ConferenceCall(auth: TelekomOAuth2Auth, client: ConferenceCallClient, title: String) {
    lazy val conferenceId = {
      val req = new CreateConferenceRequest()
      req.setOwnerId("callspark")
      req.setName(title)
      req.setDescription("Conference started by callspark")
      req.setJoinConfirm(false)
      val res = client.createConference(req)
      if (!res.getSuccess) throw new RuntimeException("conference creation failed")
      res.getConferenceId
    }
    def setParticipants(participants: Seq[Contact]) = {
      participants.zipWithIndex.foreach(addParticipant _)
    }
    def start = {
      val res = client.commitConference(conferenceId)
      if (!res.getSuccess) {
        val msg = res.getStatus.getStatusMessage
        throw new RuntimeException("failed to start the conference because %s" format msg)
      }
    }
    private def addParticipant(participant: (Contact, Int)) = participant match {
      case (c, index) =>
        val req = new NewParticipantRequest()
        req.setConferenceId(conferenceId)
        req.setNumber(c.number.get)
        c.email.map(req.setEmail(_))
        req.setIsInitiator(index == 0)
        val res = client.newParticipant(req)
        if (!res.getSuccess) {
          val msg = res.getStatus.getStatusMessage
          throw new RuntimeException("failed to add contact %s to conference call because %s".format(c.toString, msg))
        }
    }
  }
}
