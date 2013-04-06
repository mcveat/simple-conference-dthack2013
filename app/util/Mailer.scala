package util

import com.typesafe.plugin._
import play.api.Play.current
import models.Conference

/**
 * User: mcveat
 */
object Mailer {
  def sendInvitations(c: Conference) {
    val recipients = c.contacts.map(_.email).flatten
    if (recipients.isEmpty) return
    recipients.foreach(sendInvitation(c, _))
  }
  private def sendInvitation(c: Conference, r: String) {
    val mail = use[MailerPlugin].email
    mail.setSubject("You're invited to conference call on %s" format c.date)
    mail.addFrom("call-spark <no-reply@example.com>")
    mail.addRecipient(r)
    mail.sendHtml(
      """
        |<h1>You're invited to conference call on %s</h1>
        |<a href="%s">Check conference details and agenda.</a>
      """.stripMargin.format(c.date, c.agendaUrl.getOrElse(""))
    )
  }
}
