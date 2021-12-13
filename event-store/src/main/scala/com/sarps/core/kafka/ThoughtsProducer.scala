package com.sarps.core.kafka

import cats.effect.kernel.Async
import cats.effect.std.Console
import com.sarps.core.config.KafkaConfig
import com.sarps.core.domain.Thoughts
import fs2.kafka.{
  Acks,
  KafkaProducer,
  ProducerRecord,
  ProducerRecords,
  ProducerResult,
  ProducerSettings,
  Serializer
}
import io.circe.syntax._
import cats.implicits._
import fs2.Stream

trait ThoughtsProducer[F[_]] {
  def publish(event: Thoughts): F[Unit]
}

object ThoughtsProducer {
  def impl[F[_]: Async: Console](kafkaConfig: KafkaConfig): ThoughtsProducer[F] =
    new ThoughtsProducer[F] {
      val valueSerializer: Serializer[F, Thoughts] =
        Serializer[F, String].contramap[Thoughts](_.asJson.noSpaces)

      val producerSettings = ProducerSettings(
        keySerializer = Serializer[F, Unit],
        valueSerializer = valueSerializer
      ).withBootstrapServers(kafkaConfig.bootstrapServer.bootStrapServer)
        .withAcks(Acks.One)

      def publish(event: Thoughts): F[Unit] = {
        KafkaProducer
          .stream(producerSettings)
          .evalMapChunk { producer =>
            val records: ProducerRecord[Unit, Thoughts] =
              ProducerRecord(kafkaConfig.topic.topic, (), event)
            producer.produce(ProducerRecords.one(records)).flatten
          }
          .evalTap(_ => Console[F].println(s"Event sent to topic: ${kafkaConfig.topic.topic}"))
          .compile
          .drain
      }
    }

}
