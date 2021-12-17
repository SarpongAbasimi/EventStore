package com.sarps.core.es

import cats.effect.kernel.{Async, Resource}
import com.sarps.core.config.ElasticSearchConfig
import org.apache.http.HttpHost
import org.elasticsearch.client.{RestClient, RestHighLevelClient}

trait EsClientAlgebra[F[_]] {
  def client: Resource[F, RestHighLevelClient]
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
    }
}
