package com.sarps.core.kafka

import cats.effect.kernel.Async
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
  def publish(event: Thoughts): Stream[F, ProducerResult[Unit, Unit, Thoughts]]
}

object ThoughtsProducer {
  def impl[F[_]: Async](kafkaConfig: KafkaConfig): ThoughtsProducer[F] = new ThoughtsProducer[F] {
    val valueSerializer: Serializer[F, Thoughts] =
      Serializer[F, String].contramap[Thoughts](_.asJson.noSpaces)

    val producerSettings = ProducerSettings(
      keySerializer = Serializer[F, Unit],
      valueSerializer = valueSerializer
    ).withBootstrapServers(kafkaConfig.bootstrapServer.bootStrapServer)
      .withAcks(Acks.One)

    def publish(event: Thoughts): Stream[F, ProducerResult[Unit, Unit, Thoughts]] = {
      KafkaProducer.stream(producerSettings).evalMapChunk { producer =>
        val records: ProducerRecord[Unit, Thoughts] =
          ProducerRecord(kafkaConfig.topic.topic, (), event)
        producer.produce(ProducerRecords.one(records)).flatten
      }
    }
  }

}
