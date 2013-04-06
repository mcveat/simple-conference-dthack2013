package util

import play.api.Play._
import com.telekom.api.common.ServiceEnvironment

/**
 * User: mcveat
 */
object Config {
  object Evernote {
    lazy val key = current.configuration.getString("evernote.key").get
    lazy val secret = current.configuration.getString("evernote.secret").get
    lazy val callback = current.configuration.getString("evernote.callback").get
  }
  object DevGarden {
    lazy val clientId = current.configuration.getString("devgarden.clientid").get
    lazy val secret = current.configuration.getString("devgarden.secret").get
    lazy val scope = current.configuration.getString("devgarden.scope").get
    lazy val env = current.configuration.getString("devgarden.env").flatMap {
      case "production" => Some(ServiceEnvironment.PRODUCTION)
      case _ => None
    }.getOrElse(ServiceEnvironment.MOCK)
  }
}
