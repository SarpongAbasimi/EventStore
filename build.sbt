import sbt._

ThisBuild / scalaVersion := "2.13.6"


val eventStore = (project in file("./event-store"))
.settings(moduleName := "event-store")
.settings(libraryDependencies ++= Seq(
  Dependencies.catsEffets,
  Dependencies.http4sDsl,
  Dependencies.http4Server,
  Dependencies.http4sClient
))


