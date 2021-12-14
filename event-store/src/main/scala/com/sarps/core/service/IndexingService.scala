package com.sarps.core.service

import cats.effect.kernel.{Async, Resource}
import com.sarps.core.domain.Thoughts
import com.sarps.core.es.EsClientAlgebra
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.indices.CreateIndexResponse

trait IndexingService[F[_]] {
  def persistEvent(event: Thoughts): Resource[F, CreateIndexResponse]
}

object IndexingService {
  def impl[F[_]: Async](esClientAlgebra: EsClientAlgebra[F]): IndexingService[F] =
    new IndexingService[F] {
      def persistEvent(event: Thoughts): Resource[F, CreateIndexResponse] = for {
        createIndex <- Resource.eval(esClientAlgebra.createIndexRequest)
        client      <- esClientAlgebra.client
        createIndexResponse <- Resource.eval(
          Async[F].delay(client.indices().create(createIndex, RequestOptions.DEFAULT))
        )
      } yield createIndexResponse
    }
}
