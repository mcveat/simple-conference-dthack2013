package util

import play.api.Play._

/**
 * User: mcveat
 */
object Config {
  object Evernote {
    lazy val key = current.configuration.getString("evernote.key").get
    lazy val secret = current.configuration.getString("evernote.secret").get
    lazy val callback = current.configuration.getString("evernote.callback").get
  }
}
