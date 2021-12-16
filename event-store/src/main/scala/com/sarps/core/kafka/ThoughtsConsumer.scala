package com.sarps.core.kafka

import cats.effect.kernel.Async
import cats.effect.std.Console
import com.sarps.core.config.KafkaConfig
import com.sarps.core.domain.Thoughts
import fs2.kafka.{
  AutoOffsetReset,
  CommittableConsumerRecord,
  ConsumerSettings,
  Deserializer,
  KafkaConsumer
}
import io.circe.jawn.decodeByteArray
import cats.implicits._
import fs2.Stream

trait ThoughtsConsumer[F[_]] {
  def consume: Stream[F, CommittableConsumerRecord[F, Unit, Thoughts]]
}

object ThoughtsConsumer {
  def impl[F[_]: Async: Console](kafkaConfig: KafkaConfig): ThoughtsConsumer[F] =
    new ThoughtsConsumer[F] {
      override def consume: Stream[F, CommittableConsumerRecord[F, Unit, Thoughts]] = {
        implicit val valueDeserializer: Deserializer[F, Thoughts] = Deserializer.lift[F, Thoughts] {
          byteArray => decodeByteArray[Thoughts](byteArray).liftTo[F]
        }

        val consumerSettings =
          ConsumerSettings[F, Unit, Thoughts]
            .withAutoOffsetReset(AutoOffsetReset.Earliest)
            .withBootstrapServers("localhost:9092")
            .withGroupId(kafkaConfig.groupId.groupId)

        KafkaConsumer
          .stream[F, Unit, Thoughts](consumerSettings)
          .subscribeTo(kafkaConfig.topic.topic)
          .records
          .evalTapChunk { consumerRecord =>
            Console[F].println(s"Record has been processed!") *> consumerRecord.offset.commit
          }

      }
    }
}
