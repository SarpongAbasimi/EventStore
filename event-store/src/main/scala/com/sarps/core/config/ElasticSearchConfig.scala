package com.sarps.core.config

import ciris.{ConfigValue, Effect}
import ciris.env
import cats.implicits._

final case class Index(index: String) extends AnyVal
final case class Url(url: String)     extends AnyVal

case class ElasticSearchConfig(
    index: Index,
    url: Url
)

object ElasticSearchConfig {
  val elasticSearchConfig: ConfigValue[Effect, ElasticSearchConfig] =
    (
      env("INDEX").as[String].default("thoughtsIndex"),
      env("URL").as[String].default("")
    ).mapN((index, url) => ElasticSearchConfig(Index(index), Url(url)))
}
