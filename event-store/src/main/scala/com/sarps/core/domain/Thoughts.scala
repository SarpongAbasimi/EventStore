package com.sarps.core.domain

import io.circe.Codec
import java.time.Instant

import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec
import io.circe.generic.extras.semiauto.deriveUnwrappedCodec

final case class Name(name: String) extends AnyVal

object Name {
  implicit val codec: Codec[Name] = deriveUnwrappedCodec[Name]
}

final case class Date(date: Instant) extends AnyVal

object Date {
  implicit val codec: Codec[Date] = deriveUnwrappedCodec[Date]
}

final case class Message(message: String) extends AnyVal

object Message {
  implicit val codec: Codec[Message] = deriveUnwrappedCodec[Message]
}

final case class Regrets(regrets: String) extends AnyVal

object Regrets {
  implicit val codec: Codec[Regrets] = deriveUnwrappedCodec[Regrets]
}

final case class Thoughts(
    name: Name,
    message: Message,
    regrets: Option[Regrets],
    date: Option[Date]
)

object Thoughts {
  implicit val config: Configuration  = Configuration.default
  implicit val codec: Codec[Thoughts] = deriveConfiguredCodec[Thoughts]
}
