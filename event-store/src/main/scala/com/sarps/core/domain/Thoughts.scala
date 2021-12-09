package com.sarps.core.domain

import java.time.Instant

final case class Name(name: String) extends AnyVal
final case class Date(data: Instant) extends AnyVal
final case class Message(message: String) extends AnyVal
final case class Regrets(regrets: Option[String]) extends AnyVal

final case class Thoughts(
  name: Name,
  date: Date,
  message: Message,
  regrets: Regrets
)