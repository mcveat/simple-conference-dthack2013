package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import java.sql.Connection
import Conference.ContactData

/**
 * User: mcveat
 */
case class Contact(id: Long, number: Option[String], email: Option[String])

object Contact {
  def create(conferenceId: Long, contact: ContactData)(implicit c: Connection) = contact match {
    case (number, email) =>
      val id = SQL("insert into contact(conference_id, number, email) values ({conferenceId}, {number}, {email})")
        .on('conferenceId -> conferenceId, 'number -> number, 'email -> email)
        .executeInsert(scalar[Long].single)
      Contact(id, number, email)
  }
  def listFor(id: Long) = DB.withConnection { implicit c =>
    SQL("select id, number, email from contact where conference_id = {id}").on('id -> id).list(parser)
  }
  val parser = long("id") ~ get[Option[String]]("number") ~ get[Option[String]]("email") map {
    case id ~ number ~ email => Contact(id, number, email)
  }
}
