package com.sarps.core.http.routes

import cats.effect.kernel.Async
import com.sarps.core.domain.Thoughts
import com.sarps.core.kafka.ThoughtsProducer
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.circe._
import org.http4s.server.Router
import cats.implicits._

object ThoughtsRoute {
  private[routes] val prefix = "/api/v1"

  def route[F[_]: Async](producer: ThoughtsProducer[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    implicit val entityDecoder: EntityDecoder[F, Thoughts] = jsonOf[F, Thoughts]

    HttpRoutes.of[F] { case req @ POST -> Root / "thoughts" =>
      req
        .attemptAs[Thoughts]
        .foldF(
          _ => BadRequest("An error occurred Check request body"),
          event => producer.publish(event) *> Created()
        )
    }
  }

  def appRoute[F[_]: Async](producer: ThoughtsProducer[F]) = Router(prefix -> route(producer))
}
