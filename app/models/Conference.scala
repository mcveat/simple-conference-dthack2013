package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

/**
 * User: mcveat
 */
case class Conference(id: Long, date: String, agenda: String, contacts: Seq[Contact] = Seq())

object Conference {
  type ContactData = (Option[String], Option[String])
  def create(date: String, agenda: String, contacts: Seq[ContactData]) = DB.withTransaction { implicit c =>
    val id = SQL("insert into conference(date, agenda) values ({date}, {agenda})")
      .on('date -> date, 'agenda -> agenda).executeInsert(scalar[Long].single)
    Conference(id, date, agenda, contacts.map(Contact.create(id, _)))
  }
  def list = DB.withConnection { implicit c =>
    SQL("select id, date, agenda from conference order by id desc").list(parser)
  }
  val parser = long("id") ~ str("date") ~ str("agenda") map {
    case id ~ date ~ agenda => Conference(id, date, agenda, Contact.listFor(id))
  }
}
