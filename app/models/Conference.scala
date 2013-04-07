package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

/**
 * User: mcveat
 */
case class Conference(id: Long, date: String, title: String, agenda: String, contacts: Seq[Contact] = Seq(),
                      agendaUrl: Option[String] = None) {
  def initiator = contacts.find(_.initiator)
  def invited = contacts.filter(!_.initiator)
}

object ConferenceDao {
  type ContactData = (Option[String], Option[String])
  def create(date: String, title: String, agenda: String, contacts: Seq[FormContact]) =
    DB.withTransaction { implicit c =>
      val id = SQL("insert into conference(date, title, agenda) values ({date}, {title}, {agenda})")
        .on('date -> date, 'title -> title, 'agenda -> agenda).executeInsert(scalar[Long].single)
      Conference(id, date, title, agenda, contacts.map(Contact.create(id, _)))
  }
  def list = DB.withConnection { implicit c =>
    SQL("select * from conference order by id desc").list(parser)
  }
  def addAgendaUrl(c: Conference, agendaUrl: String) = DB.withConnection { implicit conn =>
    SQL("update conference set agenda_url = {agendaUrl} where id = {id}").on('agendaUrl -> agendaUrl, 'id -> c.id)
      .executeUpdate()
    c.copy(agendaUrl = Some(agendaUrl))
  }
  def find(id: Long) = DB.withConnection { implicit c =>
    SQL("select * from conference where id = {id}").on('id -> id).singleOpt(parser)
  }
  val parser = long("id") ~ str("date") ~ str("title") ~ str("agenda") ~ get[Option[String]]("agenda_url") map {
    case id ~ date ~ title ~ agenda ~ agendaUrl => Conference(id, date, title, agenda, Contact.listFor(id), agendaUrl)
  }
}
