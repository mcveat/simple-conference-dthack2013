package controllers

import play.api._
import play.api.mvc._
import models.{FormContact, ConferenceDao}
import client.Evernote
import util.Mailer
import play.api.libs.json.JsValue

object Application extends Controller {
  
  def index = Action { implicit request =>
    val result = for {
      authToken <- request.session.get("evernote-auth-token")
    } yield {
      Ok(views.html.newConference())
    }
    result getOrElse Ok(views.html.evernoteLogin())
  }

  def newConference = Action { implicit request =>
    val result = request.body.asJson.flatMap { json =>
      for {
        date <- (json \ "date").asOpt[String]
        title <- (json \ "title").asOpt[String]
        agenda <- (json \ "agenda").asOpt[String]
        contacts <- (json \ "contacts").asOpt[Array[JsValue]]
        noteUrl <- request.session.get("evernote-note-url")
        authToken <- request.session.get("evernote-auth-token")
        userShard <- request.session.get("evernote-user-shard")
      } yield {
        val c = ConferenceDao.create(date, title, agenda, extractContacts(contacts))
        val agendaUrl = Evernote.storeAgenda(noteUrl, authToken, userShard, c)
        val cc = ConferenceDao.addAgendaUrl(c, agendaUrl)
        Mailer.sendInvitations(cc)
        Ok
      }
    }
    result getOrElse BadRequest
  }

  def resetSession = Action {
    SeeOther(routes.Application.index.url).withNewSession
  }

  private def extractContacts(contacts: Array[JsValue]) = contacts.map(js2FormContact(_)).toSeq

  def js2FormContact(json: JsValue): FormContact = {
    val name = (json \ "name").as[String]
    val email = (json \ "email").asOpt[String]
    val phone = (json \ "phone").as[String]
    val initiator = (json \ "initiator").as[Boolean]
    FormContact(phone, name, email, initiator)
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        routes.javascript.Application.newConference,
        routes.javascript.Telekom.startConference
      )
    ).as("text/javascript")
  }
  
}
