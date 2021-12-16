package com.sarps.core.es

import cats.effect.kernel.{Async, Resource}
import com.sarps.core.config.ElasticSearchConfig
import com.sarps.core.domain.Thoughts
import org.apache.http.HttpHost
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.indices.CreateIndexRequest
import org.elasticsearch.client.{RestClient, RestHighLevelClient}
import org.elasticsearch.common.settings.Settings
import io.circe.syntax._
import org.elasticsearch.common.xcontent.XContentType

trait EsClientAlgebra[F[_]] {
  def client: Resource[F, RestHighLevelClient]
  def createIndexRequest: F[CreateIndexRequest]
  def index(event: Thoughts): F[IndexRequest]
}

object EsClientAlgebra {
  def impl[F[_]: Async](esConfig: ElasticSearchConfig): EsClientAlgebra[F] =
    new EsClientAlgebra[F] {
      def client: Resource[F, RestHighLevelClient] = Resource.make(
        Async[F].delay(
          new RestHighLevelClient(
            RestClient.builder(
              new HttpHost(esConfig.host.host, esConfig.port.port, esConfig.scheme.scheme)
            )
          )
        )
      )(client => Async[F].delay(client.close()))

      def createIndexRequest: F[CreateIndexRequest] = Async[F].delay {
        new CreateIndexRequest(esConfig.index.name)
          .settings(
            Settings
              .builder()
              .put("index.number_of_shards", esConfig.shards.value)
              .put("index.number_of_replicas", esConfig.replicas.value)
          )
      }

      def index(event: Thoughts): F[IndexRequest] = Async[F].delay(
        new IndexRequest(esConfig.index.name).source(event.asJson.noSpaces, XContentType.JSON)
      )
    }
}
