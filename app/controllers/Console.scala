package controllers

import play.api._
import play.api.mvc._
import models.ConferenceDao

/**
 * User: mcveat
 */
object Console extends Controller {
  def index = Action {
    Ok(views.html.console(ConferenceDao.list))
  }
}
