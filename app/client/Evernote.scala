package client

import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.EvernoteApi.Sandbox
import util.Config
import org.scribe.model.{Verifier, Token}

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
    (authToken.getToken, authToken.getNoteStoreUrl)
  }
}
