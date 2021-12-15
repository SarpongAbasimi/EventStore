package com.sarps.core.service

import cats.effect.kernel.{Async, Resource}
import cats.effect.std.Console
import com.sarps.core.domain.Thoughts
import com.sarps.core.es.EsClientAlgebra
import com.sarps.core.kafka.ThoughtsConsumer
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.indices.CreateIndexResponse
import fs2.Stream

trait IndexingService[F[_]] {
  def persist: Stream[F, Unit]
}

object IndexingService {
  def impl[F[_]: Async: Console](
      esClientAlgebra: EsClientAlgebra[F],
      consumer: ThoughtsConsumer[F]
  ): IndexingService[F] =
    new IndexingService[F] {

      def persist: Stream[F, Unit] = {
        consumer.consume.evalMapChunk(consumerRecord => {
          persistEvent(consumerRecord.record.value).use { response =>
            Console[F].println(
              s"Message has been stored in E: isAcknowledged -> ${response.isAcknowledged}"
            )
          }
        })
      }

      private[service] def persistEvent(event: Thoughts): Resource[F, CreateIndexResponse] = for {
        createIndex <- Resource.eval(esClientAlgebra.createIndexRequest)
        client      <- esClientAlgebra.client
        createIndexResponse <- Resource.eval(
          Async[F].delay(client.indices().create(createIndex, RequestOptions.DEFAULT))
        )
      } yield createIndexResponse
    }
}
