package util

import com.typesafe.plugin._
import play.api.Play.current
import models.Conference
import play.api.mvc.{Request, AnyContent}

/**
 * User: mcveat
 */
object Mailer {
  def sendInvitations(c: Conference)(implicit request: Request[AnyContent]) {
    val recipients = c.contacts.map(_.email).flatten
    if (recipients.isEmpty) return
    recipients.foreach(sendInvitation(c, _))
  }
  private def sendInvitation(c: Conference, r: String)(implicit request: Request[AnyContent]) {
    val mail = use[MailerPlugin].email
    mail.setSubject("You're invited to conference call on %s" format c.date)
    mail.addFrom("call-spark <no-reply@example.com>")
    mail.addRecipient(r)
    mail.sendHtml(views.html.confirmationEmail(c).toString)
  }
}
