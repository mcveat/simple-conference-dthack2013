import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "simple-conference-dt2013"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "com.evernote" % "evernote-api" % "1.23",
    "org.scribe" % "scribe" % "1.3.3",
    "com.typesafe" %% "play-plugins-mailer" % "2.1.0",
    jdbc,
    anorm
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
