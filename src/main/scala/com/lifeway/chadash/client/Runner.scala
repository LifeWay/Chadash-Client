package com.lifeway.chadash.client

import com.lifeway.chadash.client.deploy.Deploy
import com.lifeway.chadash.client.monitor.MonitorDeploy

object Runner extends App {

  import com.lifeway.chadash.client.deploy.Deploy._

  args.length match {
    case x if x > 0 =>
      args(0) match {
        case "deploy" => deploy(args.tail)
        case "monitor" => monitor(args.tail)
        case _ => throw throw new IllegalArgumentException("You must specify either `deploy` or `monitor` as the command")
      }
    case _ => throw new IllegalArgumentException("No arguments were given")
  }

  def deploy(args: Array[String]) = {
    if (args.length != 5)
      throw new IllegalArgumentException("Params: username password imageId version stackPath [timeoutmin]")
    args.length match {
      case 5 => Deploy(DeployConfig(args(0), args(1), args(2), args(3), args(4)))
      case _ => Deploy(DeployConfig(args(0), args(1), args(2), args(3), args(4), args(5).toInt)) //let it fail, if the user didn't give us a number.
    }
  }

  def monitor(args: Array[String]) = {
    if (args.length != 2)
      throw new IllegalArgumentException("Params: stackPath version")

    MonitorDeploy(args(0), args(1))
  }
}
