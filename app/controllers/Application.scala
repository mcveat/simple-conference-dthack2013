package controllers

import play.api._
import play.api.mvc._
import models.Conference

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def newConference = Action { implicit request =>
    val result = request.body.asJson.flatMap { json =>
      for {
        date <- (json \ "date").asOpt[String]
        agenda <- (json \ "agenda").asOpt[String]
        contacts <- (json \ "contacts").asOpt[String]
      } yield {
        Conference.create(date, agenda, extractContacts(contacts))
        Ok
      }
    }
    result getOrElse BadRequest
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
