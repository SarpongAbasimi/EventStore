package com.sarps.core.server

import cats.effect.ExitCode
import cats.effect.kernel.Async
import com.sarps.core.config.Config
import com.sarps.core.http.routes.ThoughtsRoute
import org.http4s.blaze.server.BlazeServerBuilder

object Server {
  def stream[F[_]: Async](config: Config): fs2.Stream[F, ExitCode] = for {
    server <- BlazeServerBuilder[F]
      .bindHttp(config.serverConfig.port.port, config.serverConfig.host.host)
      .withHttpApp(ThoughtsRoute.appRoute[F].orNotFound)
      .serve
  } yield server
}
