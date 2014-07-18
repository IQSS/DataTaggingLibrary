name := """DataTags-app"""

version := "1.0-SNAPSHOT"

organization := "edu.harvard.iq"

libraryDependencies ++= Seq(
  cache,
  "org.scalatestplus" % "play_2.10" % "1.0.0" % "test"
)

play.Project.playScalaSettings