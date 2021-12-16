package com.sarps.core.service

import cats.effect.kernel.{Async}
import cats.effect.std.Console
import com.sarps.core.es.EsClientAlgebra
import com.sarps.core.kafka.ThoughtsConsumer
import fs2.Stream

trait IndexingService[F[_]] {
  def persist: Stream[F, Unit]
}

object IndexingService {
  def impl[F[_]: Async: Console](
      esClient: EsClientAlgebra[F],
      consumer: ThoughtsConsumer[F]
  ): IndexingService[F] =
    new IndexingService[F] {

      def persist: Stream[F, Unit] = {
        consumer.consume.evalMapChunk { _ =>
          Async[F].delay()
        }
      }
    }
}
