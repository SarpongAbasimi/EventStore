package com.sarps.core

import cats.effect.IOApp
import cats.effect.{ExitCode, IO}
import com.sarps.core.config.Config
import fs2.{Stream => Fs2Stream}
import com.sarps.core.server.Server

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = (for {
    config <- Fs2Stream.eval(Config.config.load[IO])
    server <- Server.stream[IO](config)
  } yield server).compile.drain.as(ExitCode.Success)

}
