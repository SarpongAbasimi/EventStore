package com.sarps.core.config

import ciris.{ConfigValue, Effect}
import cats.implicits._

final case class Config(
    serverConfig: ServerConfig,
    kafkaConfig: KafkaConfig,
    elasticSearchConfig: ElasticSearchConfig
)

object Config {
  val config: ConfigValue[Effect, Config] =
    (ServerConfig.serverConfig, KafkaConfig.kafkaConfig, ElasticSearchConfig.elasticSearchConfig)
      .parMapN { (serverConfig, kafkaConfig, esConfig) =>
        Config(serverConfig, kafkaConfig, esConfig)
      }
}
