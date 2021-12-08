import sbt._

object Dependencies {
  private val catsEffectVersion = "3.3.0"
  private val http4sVersion     = "0.23.7"

  def http4s(branch: String) =
    "org.http4s" %% s"http4s-$branch" % http4sVersion

  val catsEffets   = "org.typelevel" %% "cats-effect" % catsEffectVersion
  val http4sDsl    = http4s("dsl")
  val http4Server  = http4s("blaze-server")
  val http4sClient = http4s("blaze-client")
}
