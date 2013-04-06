package controllers

import play.api._
import play.api.mvc._
import models.ConferenceDao
import client.Evernote

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
        agenda <- (json \ "agenda").asOpt[String]
        contacts <- (json \ "contacts").asOpt[String]
        noteUrl <- request.session.get("evernote-note-url")
        authToken <- request.session.get("evernote-auth-token")
        userShard <- request.session.get("evernote-user-shard")
      } yield {
        val c = ConferenceDao.create(date, agenda, extractContacts(contacts))
        val agendaUrl = Evernote.storeAgenda(noteUrl, authToken, userShard, c)
        ConferenceDao.addAgendaUrl(c, agendaUrl)
        Ok
      }
    }
    result getOrElse BadRequest
  }

  def resetSession = Action {
    SeeOther(routes.Application.index.url).withNewSession
  }

  private def extractContacts(s: String) = {
    s.lines.map(_.split(" ").toList).map {
      case number :: email :: Nil => Some((Some(number), Some(email)))
      case number :: Nil => Some((Some(number), None))
      case _ => None
    }.toList.flatten
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        routes.javascript.Application.newConference
      )
    ).as("text/javascript")
  }
  
}
