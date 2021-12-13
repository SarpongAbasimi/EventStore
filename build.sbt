import sbt._

ThisBuild / scalaVersion := "2.13.6"

val eventStore = (project in file("./event-store"))
  .settings(moduleName := "event-store")
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.ciris,
      Dependencies.http4sDsl,
      Dependencies.http4Server,
      Dependencies.catsEffects,
      Dependencies.http4sClient,
      Dependencies.circeGenericExtra,
      Dependencies.http4sCirce,
      Dependencies.fs2Kafka,
      Dependencies.catsEffectsStd
    ) ++ Dependencies.circe
  )
