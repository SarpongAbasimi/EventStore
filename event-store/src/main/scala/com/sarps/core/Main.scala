package com.sarps.core

import cats.effect.IOApp
import cats.effect.{ExitCode, IO}
import cats.effect.std.Console
import com.sarps.core.config.Config
import com.sarps.core.es.EsClientAlgebra
import fs2.{Stream => Fs2Stream}
import com.sarps.core.server.Server
import org.elasticsearch.client.RequestOptions
import cats.implicits._

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = (for {
    config          <- Fs2Stream.eval(Config.config.load[IO])
    esClientAlgebra <- Fs2Stream.emit(EsClientAlgebra.impl[IO](config.elasticSearchConfig))
    restClient      <- Fs2Stream.resource(esClientAlgebra.client)
    index           <- Fs2Stream.eval(EsClientAlgebra.impl[IO](config.elasticSearchConfig).createIndexRequest)
    _ <- Fs2Stream.eval(
      IO(restClient.indices().create(index, RequestOptions.DEFAULT)) *> Console[IO].println(
        s"Index with name ${index.index()} has been created 🚀"
      )
    )
    server <- Server.stream[IO](config, restClient, esClientAlgebra)
  } yield server).compile.drain.as(ExitCode.Success)

}
