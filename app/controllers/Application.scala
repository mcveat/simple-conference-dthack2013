package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def newConference = Action { implicit request =>
    println(request.body.asJson)
    Ok
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        routes.javascript.Application.newConference
      )
    ).as("text/javascript")
  }
  
}
