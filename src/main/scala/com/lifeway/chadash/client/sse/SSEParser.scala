package com.lifeway.chadash.client.sse

import org.scalactic.{Bad, Good, Or}

import scala.util.parsing.combinator._

case class SSEEvent(event: Option[String], data: String)

object SSEParser extends RegexParsers {

  def event: Parser[Option[String]] = """event: (.*)""".r ^^ (x => Some(x.substring(7)))

  def data: Parser[String] = """data: (.*)""".r ^^ (x => x.substring(6))

  def sse: Parser[SSEEvent] = event.? ~ data ^^ {
    case Some(event) ~ data => SSEEvent(event, data)
    case None ~ data => SSEEvent(None, data)
  }

  def parse(input: String): SSEEvent Or String = {
    parse(sse, input.trim()) match {
      case Success(result, _) => Good(result)
      case Failure(msg, _) => Bad(msg)
      case Error(msg, _) => Bad(msg)
    }
  }
}