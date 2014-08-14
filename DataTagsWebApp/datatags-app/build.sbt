name := """DataTags-app"""

version := "1.0-SNAPSHOT"

organization := "edu.harvard.iq"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

LessKeys.compress in Assets := true

TwirlKeys.templateImports += "views.Helpers"

libraryDependencies ++= Seq(
  cache,
  ws,
  "org.scalatestplus" % "play_2.11" % "1.2.0" % "test"
)

includeFilter in (Assets, LessKeys.less) := "*.less"

// add "com.typesafe.sbt" % "sbt-gzip" % "1.0.0" 
// add "com.typesafe.sbt" % "sbt-digest" % "1.0.0"

