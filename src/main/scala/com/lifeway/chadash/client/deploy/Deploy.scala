package com.lifeway.chadash.client.deploy

import com.typesafe.config.ConfigFactory
import play.api.libs.json.{JsString, Json}
import play.api.libs.ws.{WS, WSAuthScheme}

import scala.concurrent.Await
import scala.concurrent.duration._

object Deploy {
  def apply(deploy: DeployConfig): Unit = {
    val conf = ConfigFactory.load()
    val chadashUrl = conf.getString("chadash.url")
    val url = s"$chadashUrl/api/deploy/${deploy.stackPath}"

    val jsonBody = Json.obj(
      "ami_id" -> JsString(deploy.amiId),
      "version" -> JsString(deploy.version)
    )

    WSClient { implicit client =>
      val response = WS.clientUrl(url)
                     .withQueryString("timeoutmin" -> deploy.timeoutMin.toString)
                     .withHeaders("Content-Type" -> "application/json")
                     .withAuth(deploy.user, deploy.pw, WSAuthScheme.BASIC)
                     .post(jsonBody)
      val blockedResponse = Await.result(response, 5.seconds)
      println(blockedResponse.body)
      if (blockedResponse.status != 200) throw DeployRequestFailed
    }
  }


  case class DeployConfig(user: String, pw: String, amiId: String, version: String, stackPath: String,
                          timeoutMin: Int = 30)

  case object DeployRequestFailed extends Exception

}
