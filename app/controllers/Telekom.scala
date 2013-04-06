package controllers

import play.api._
import play.api.mvc._
import client.DevGarden
import models.ConferenceDao

/**
 * User: mcveat
 */
object Telekom extends Controller {
  def startConference(id: Int) = Action {
    ConferenceDao.find(id).map { c =>
      DevGarden.startConference(c)
      Ok
    }.getOrElse(BadRequest)
  }
}
