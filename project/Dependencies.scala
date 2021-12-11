import sbt._

object Dependencies {
  private val catsEffectVersion        = "3.3.0"
  private val http4sVersion            = "0.23.7"
  private val cirisVersion             = "2.3.1"
  private val circeVersion             = "0.14.1"
  private val circeGenericExtraVersion = "0.14.1"

  def http4s(branch: String) =
    "org.http4s" %% s"http4s-$branch" % http4sVersion

  val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)

  val ciris             = "is.cir"        %% "ciris"                % cirisVersion
  val catsEffects       = "org.typelevel" %% "cats-effect"          % catsEffectVersion
  val circeGenericExtra = "io.circe"      %% "circe-generic-extras" % circeGenericExtraVersion
  val http4sDsl         = http4s("dsl")
  val http4Server       = http4s("blaze-server")
  val http4sClient      = http4s("blaze-client")
  val http4sCirce       = http4s("circe")
}
