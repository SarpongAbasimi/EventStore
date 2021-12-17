package com.sarps.core.es

import cats.effect.kernel.Async
import cats.effect.std.Console
import com.sarps.core.config.ElasticSearchConfig
import com.sarps.core.domain.Thoughts
import org.elasticsearch.action.index.{IndexRequest, IndexResponse}
import org.elasticsearch.client.indices.{CreateIndexRequest, GetIndexRequest}
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.xcontent.XContentType
import io.circe.syntax._
import org.elasticsearch.client.{RequestOptions, RestHighLevelClient}
import cats.implicits._

trait EsAlgebra[F[_]] {
  def createIndex: F[Unit]
  def indexRecord(event: Thoughts): F[IndexResponse]
}

object EsAlgebra {
  def impl[F[_]: Async: Console](
      esConfig: ElasticSearchConfig,
      restClient: RestHighLevelClient
  ): EsAlgebra[F] = new EsAlgebra[F] {

    def createIndex: F[Unit] = for {
      getIndex  <- getIndexRequest
      indexExit <- Async[F].delay(restClient.indices().exists(getIndex, RequestOptions.DEFAULT))
      request   <- createIndexRequest
      _ <- indexExit
        .pure[F]
        .ifM(
          Console[F].println("Index already exists 🚨"),
          Async[F].delay(restClient.indices().create(request, RequestOptions.DEFAULT)) *> Console[F]
            .println(s"Index with name ${esConfig.index.name} has been created 🚀")
        )
    } yield ()

    def indexRecord(event: Thoughts): F[IndexResponse] = Async[F].delay(
      restClient.index(
        new IndexRequest(esConfig.index.name).source(event.asJson.noSpaces, XContentType.JSON),
        RequestOptions.DEFAULT
      )
    )

    private[es] def createIndexRequest: F[CreateIndexRequest] = Async[F].delay {
      new CreateIndexRequest(esConfig.index.name)
        .settings(
          Settings
            .builder()
            .put("index.number_of_shards", esConfig.shards.value)
            .put("index.number_of_replicas", esConfig.replicas.value)
        )
    }

    private[es] def getIndexRequest: F[GetIndexRequest] =
      Async[F].delay(new GetIndexRequest(esConfig.index.name))
  }
}
