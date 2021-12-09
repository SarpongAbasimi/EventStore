package com.sarps.core.config

import ciris.{ConfigValue, Effect}
import cats.implicits._

final case class Config(serverConfig: ServerConfig, kafkaConfig: KafkaConfig)

object Config {
  val config: ConfigValue[Effect, Config] =
    (ServerConfig.serverConfig, KafkaConfig.kafkaConfig).parMapN { (serverConfig, kafkaConfig) =>
      Config(serverConfig, kafkaConfig)
    }
}
