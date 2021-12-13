package com.sarps.core.server

import cats.effect.ExitCode
import cats.effect.kernel.Async
import cats.effect.std.Console
import com.sarps.core.config.Config
import com.sarps.core.http.routes.ThoughtsRoute
import com.sarps.core.kafka.ThoughtsProducer
import org.http4s.blaze.server.BlazeServerBuilder
import fs2.Stream

object Server {
  def stream[F[_]: Async: Console](config: Config): fs2.Stream[F, ExitCode] = for {
    _ <- Stream.eval(Console[F].println("Starting Server 🚀"))

    producer: ThoughtsProducer[F] = ThoughtsProducer.impl[F](config.kafkaConfig)

    server <- BlazeServerBuilder[F]
      .bindHttp(config.serverConfig.port.port, config.serverConfig.host.host)
      .withHttpApp(ThoughtsRoute.appRoute[F](producer).orNotFound)
      .serve
  } yield server
}
