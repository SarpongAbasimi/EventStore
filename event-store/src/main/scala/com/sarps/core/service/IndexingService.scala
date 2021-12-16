package com.sarps.core.service

import cats.effect.kernel.Async
import cats.effect.std.Console
import com.sarps.core.es.EsClientAlgebra
import com.sarps.core.kafka.ThoughtsConsumer
import org.elasticsearch.client.{RequestOptions, RestHighLevelClient}
import fs2.Stream
import cats.syntax.functor._
import org.elasticsearch.action.index.IndexResponse

trait IndexingService[F[_]] {
  def persist: Stream[F, IndexResponse]
}

object IndexingService {
  def impl[F[_]: Async: Console](
      esClient: EsClientAlgebra[F],
      consumer: ThoughtsConsumer[F],
      restClient: RestHighLevelClient
  ): IndexingService[F] =
    new IndexingService[F] {

      def persist: Stream[F, IndexResponse] = {
        consumer.consume
          .evalMapChunk { consumedEvents =>
            val indexRequest = esClient.index(consumedEvents.record.value)
            indexRequest.map(request => restClient.index(request, RequestOptions.DEFAULT))
          }
          .evalTapChunk(indexResponse =>
            Console[F].println(s"The index status is ->  ${indexResponse.status()}")
          )
      }
    }
}
