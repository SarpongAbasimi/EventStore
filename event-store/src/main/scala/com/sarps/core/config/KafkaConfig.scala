package com.sarps.core.config

import ciris.{env, ConfigValue, Effect}
import com.sarps.core.domain.{BootStrapServer, GroupId, Topic}
import cats.implicits._

case class KafkaConfig(bootstrapServer: BootStrapServer, groupId: GroupId, topic: Topic)

object KafkaConfig {
  val kafkaConfig: ConfigValue[Effect, KafkaConfig] = (
    env("BOOTSTRAP_SERVER").as[String].default(""),
    env("GROUP_ID").as[String].default("consumerGroup"),
    env("KAFKA_TOPIC").as[String].default("thoughtsTopic")
  ).parMapN((port, groupId, topic) =>
    KafkaConfig(BootStrapServer(port), GroupId(groupId), Topic(topic))
  )
}
