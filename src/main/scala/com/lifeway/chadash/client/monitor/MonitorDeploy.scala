package com.lifeway.chadash.client.monitor

import com.lifeway.chadash.client.deploy.WSClient
import com.lifeway.chadash.client.sse.SSEParser
import com.typesafe.config.ConfigFactory
import org.scalactic.{Bad, Good}
import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.libs.ws.{WS, WSResponseHeaders}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

object MonitorDeploy {

  def iterateeHandler = Iteratee.foreach[Array[Byte]] { chunk =>
    val eventBody = new String(chunk)
    val eventBodies = eventBody.trim().split( """\n\n""")
    eventBodies.map { event =>
      SSEParser.parse(event) match {
        case Good(x) =>
          println(x.data)

          for {
            event <- x.event
          } yield event match {
            case "Success" => Runtime.getRuntime.halt(0)
            case "Fail" => Runtime.getRuntime.halt(1)
            case _ => ()
          }
        case Bad(x) => println("Received something that was not a SSEEvent: " + x)
      }
    }
  }

  def apply(stackPath: String, version: String) = {
    val conf = ConfigFactory.load()
    val chadashUrl = conf.getString("chadash.url")
    val url = s"$chadashUrl/api/sse-log/$stackPath"

    WSClient { implicit client =>
      val responseStream: Future[(WSResponseHeaders, Enumerator[Array[Byte]])] = WS.clientUrl(url)
                                                                                 .withQueryString("version" -> version)
                                                                                 .withRequestTimeout(3600000)
                                                                                 .getStream()
      val f = responseStream.flatMap {
        case (headers, body) =>
          if (headers.status == 200)
            body |>>> iterateeHandler // Feed the body into the iteratee
          else
            body(Iteratee.foreach(x => println(new String(x))))
      }

      //Fail if we haven't finished in the hour.. the API will timeout itself based on the timeout config.
      Await.result(f, 1.hour)
      Runtime.getRuntime.halt(1)
    }
  }
}
