package com.lifeway.chadash.client.deploy

import com.ning.http.client.AsyncHttpClientConfig
import play.api.libs.ws.DefaultWSClientConfig
import play.api.libs.ws.ning.{NingAsyncHttpClientConfigBuilder, NingWSClient}

object WSClient {
  def apply(block: NingWSClient => Unit): Unit = {
    val clientConfig = new DefaultWSClientConfig()
    val secureDefaults = new NingAsyncHttpClientConfigBuilder(clientConfig).build()
    val builder = new AsyncHttpClientConfig.Builder(secureDefaults)
    builder.setCompressionEnabled(false) //MUST BE FALSE FOR SSE CONSUMER TO WORK!
    builder.setIdleConnectionTimeoutInMs(3600000)
    val secureDefaultsWithSpecificOptions = builder.build()
    val client = new NingWSClient(secureDefaultsWithSpecificOptions)

    try {
      block(client)
    } finally {
      client.close()
    }
  }
}
