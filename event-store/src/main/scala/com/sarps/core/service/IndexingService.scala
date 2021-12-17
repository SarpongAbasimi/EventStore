package com.sarps.core.service

import cats.effect.kernel.Async
import cats.effect.std.Console
import com.sarps.core.es.{EsAlgebra}
import com.sarps.core.kafka.ThoughtsConsumer
import org.elasticsearch.client.{RestHighLevelClient}
import fs2.Stream
import org.elasticsearch.action.index.IndexResponse

trait IndexingService[F[_]] {
  def persist: Stream[F, IndexResponse]
}

object IndexingService {
  def impl[F[_]: Async: Console](
      consumer: ThoughtsConsumer[F],
      restClient: RestHighLevelClient,
      esAlgebra: EsAlgebra[F]
  ): IndexingService[F] =
    new IndexingService[F] {

      def persist: Stream[F, IndexResponse] = {
        consumer.consume
          .evalMapChunk { consumedEvents =>
            esAlgebra.indexRecord(consumedEvents.record.value)
          }
          .evalTapChunk(indexResponse =>
            Console[F].println(s"The index status is ->  ${indexResponse.status()}")
          )
      }
    }
}
