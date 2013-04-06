package controllers

import play.api.mvc._
import client.Evernote

/**
 * User: mcveat
 */
object EvernoteAuth extends Controller {
  def evernoteLogin = Action {
    val (token, secret) = Evernote.getRequestToken
    SeeOther(Evernote.oauthTokenUrl(token))
      .withSession("evernote-request-token" -> token, "evernote-request-secret" -> secret)
  }

  def evernoteCallback = Action { implicit request =>
    val qs = request.queryString
    val result = for {
      requestToken <- request.session.get("evernote-request-token")
      requestSecret <- request.session.get("evernote-request-secret")
      oauthToken <- qs.get("oauth_token").flatMap(_.headOption)
      oauthVerifier <- qs.get("oauth_verifier").flatMap(_.headOption)
    } yield {
      val (authToken, noteUrl) = Evernote.getAccessToken(requestToken, requestSecret, oauthVerifier)
      SeeOther(routes.Application.index.url).withSession(
        request.session + ("evernote-oauth-token" -> oauthToken) + ("evernote-oauth-verifier" -> oauthVerifier) +
          ("evernote-auth-token" -> authToken) + ("evernote-note-url" -> noteUrl)
      )
    }
    result getOrElse BadRequest
  }
}
