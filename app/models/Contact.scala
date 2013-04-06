package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import java.sql.Connection
import ConferenceDao.ContactData

/**
 * User: mcveat
 */
case class Contact(id: Long, number: String, name: String, email: Option[String], initiator: Boolean)
case class FormContact(number: String, name: String, email: Option[String], initiator: Boolean) {
  def withId(id: Long) = Contact(id, number, name, email, initiator)
}

object Contact {
  def create(conferenceId: Long, contact: FormContact)(implicit c: Connection) = contact match {
    case FormContact(number, name, email, initiator) =>
      val id = SQL(
          "insert into contact(conference_id, number, name, email, initiator) " +
          "values ({conferenceId}, {number}, {name}, {email}, {initiator})")
        .on('conferenceId -> conferenceId, 'number -> number, 'name -> name, 'email -> email, 'initiator -> initiator)
        .executeInsert(scalar[Long].single)
      contact.withId(id)
  }
  def listFor(id: Long) = DB.withConnection { implicit c =>
    SQL("select * from contact where conference_id = {id}").on('id -> id).list(parser)
  }
  val parser = long("id") ~ str("number") ~ str("name") ~ get[Option[String]]("email") ~ bool("initiator") map {
    case id ~ number ~ name ~ email ~ initiator => Contact(id, number, name, email, initiator)
  }
}
