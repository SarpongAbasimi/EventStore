package com.sarps.core

import cats.effect.IOApp
import cats.effect.{ExitCode, IO}
import com.sarps.core.config.{Config}
import com.sarps.core.es.{EsAlgebra, EsClientAlgebra}
import fs2.{Stream => Fs2Stream}
import com.sarps.core.server.Server

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = (for {
    config          <- Fs2Stream.eval(Config.config.load[IO])
    esClientAlgebra <- Fs2Stream.emit(EsClientAlgebra.impl[IO](config.elasticSearchConfig))
    restHighClient  <- Fs2Stream.resource(esClientAlgebra.client)
    esAlgebra       <- Fs2Stream.emit(EsAlgebra.impl[IO](config.elasticSearchConfig, restHighClient))
    _               <- Fs2Stream.eval(esAlgebra.createIndex)
    server          <- Server.stream[IO](config, restHighClient, esClientAlgebra, esAlgebra)
  } yield server).compile.drain.as(ExitCode.Success)

}
