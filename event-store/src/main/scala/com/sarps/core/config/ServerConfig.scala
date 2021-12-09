package com.sarps.core.config
import cats.implicits._
import ciris._
import com.sarps.core.domain.{Host, Port}

final case class ServerConfig(
    port: Port,
    host: Host
)

object ServerConfig {
  val serverConfig: ConfigValue[Effect, ServerConfig] = (
    env("PORT").as[Int].default(8888),
    env("HOST").as[String].default("localhost")
  ).parMapN((port, host) => ServerConfig(Port(port), Host(host)))
}
