package com.sarps.core.config

import ciris.{env, ConfigValue, Effect}
import com.sarps.core.domain.{BootStrapServer, GroupId, Host, Port}
import cats.implicits._

case class KafkaConfig(bootstrapServer: BootStrapServer, groupId: GroupId)

object KafkaConfig {
  val kafkaConfig: ConfigValue[Effect, KafkaConfig] = (
    env("BOOTSTRAP_SERVER").as[String].default(""),
    env("GROUP_ID").as[String].default("consumerGroup")
  ).parMapN((port, groupId) => KafkaConfig(BootStrapServer(port), GroupId(groupId)))
}
