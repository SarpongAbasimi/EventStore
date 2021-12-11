package com.sarps.core.http.routes

import cats.effect.kernel.Async
import com.sarps.core.domain.Thoughts
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.circe._
import org.http4s.server.Router

object ThoughtsRoute {
  private[routes] val prefix = "/api/v1"

  private def route[F[_]: Async]: HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    implicit val entityDecoder: EntityDecoder[F, Thoughts] = jsonOf[F, Thoughts]

    HttpRoutes.of[F] { case req @ POST -> Root / "thoughts" =>
      req
        .attemptAs[Thoughts]
        .foldF(_ => BadRequest("An error occurred Check request body"), _ => Created())
    }
  }

  def appRoute[F[_]: Async] = Router(prefix -> route)
}
