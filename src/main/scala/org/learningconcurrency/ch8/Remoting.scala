package org.learningconcurrency
package ch8



import akka.actor._
import akka.event.Logging
import scala.concurrent.duration._
import scala.util.{Failure, Success}


object RemotingPongySystem extends App {
  val system = remotingSystem("PongyDimension", 24321)
  val pongy = system.actorOf(Props[Pongy], "pongy")
  Thread.sleep(Long.MaxValue)
  system.shutdown()
}

class Runner extends Actor {
  val log = Logging(context.system, this)
  val pingy = context.actorOf(Props[Pingy], "pingy")

import scala.concurrent.ExecutionContext.Implicits.global
  def receive = {
    case "start" =>
      val path = context.actorSelection("akka.tcp://PongyDimension@127.0.0.1:24321/user/pongy")
      path ! Identify(0)
    case "resolveOne" =>
      context.actorSelection("akka.tcp://PongyDimension@127.0.0.1:24321/user/pongy").resolveOne(2 seconds) onComplete {
        case Success(ref) => pingy ! ref
        case Failure(t)=> log.info(s"no pongy found", t)
      }
    case ActorIdentity(0, Some(ref)) =>
      pingy ! ref
    case ActorIdentity(0, None) =>
      log.info("Something's wrong -- no pongy anywhere!")
      context.stop(self)
    case "pong" =>
      log.info("got a pong from another dimension.")
      context.stop(self)
  }
}

object RemotingPingySystem extends App {
  val system = remotingSystem("PingyDimension", 24567)
  val runner = system.actorOf(Props[Runner], "runner")
//  runner ! "start"
  runner ! "resolveOne"
  Thread.sleep(Long.MaxValue)
  system.shutdown()
}

