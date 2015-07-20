package org.learningconcurrency.exercises.ch8

import akka.actor.{ActorRef, Props, Actor}
import akka.actor.Actor.Receive
import akka.event.Logging
import org.learningconcurrency.ch8._
import org.learningconcurrency.exercises.ch8.TimerActor.Register
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by ian on 17/07/15.
 */

object TimerActor {
  case class Register(timeout: Long)
  case class Timeout(timeout: Long)
}

class TimerActor extends Actor {
  val log = Logging(context.system, this)

  def receive: Receive = {
    case Register(t) => {
      val currentSender = sender //NOTE changing this to a def makes the sender 'deadletters'
      log.info(s"Register [$t] from [${currentSender.path.name}}]")
      context.system.scheduler.scheduleOnce(t milliseconds) {
//      log.info(s"timeout happened [$t] currentSender [${currentSender.path.name}] sender [${sender.path.name}]       ")
        currentSender ! TimerActor.Timeout(t)
      }
    }
  }

}

object TimerClient {
  def props(timerActor: ActorRef ) : Props = Props( new TimerClient(timerActor))
}

class TimerClient(timerActor: ActorRef) extends Actor {
  val log = Logging(context.system, this)

  def receive: Receive = {
    case r @ Register(_) => timerActor ! r
    case TimerActor.Timeout(t) => log.info(s"timeout of [$t] hit.")
  }


}

object TimerTester extends App {
  val timer = ourSystem.actorOf(Props[TimerActor], "sharedTimer")

  val timerClient1 = ourSystem.actorOf(TimerClient.props(timer), "massiveClient1")
  val timerClient2 = ourSystem.actorOf(TimerClient.props(timer), "massiveClient2")
  val timerClient3 = ourSystem.actorOf(TimerClient.props(timer), "massiveClient3")

  timerClient1 ! Register(900)
  timerClient2 ! Register(800)
  timerClient3 ! Register(700)
  timerClient1 ! Register(600)
  timerClient2 ! Register(500)
  timerClient3 ! Register(400)
  timerClient1 ! Register(300)
  timerClient2 ! Register(200)
  timerClient3 ! Register(100)

  Thread.sleep(Long.MaxValue)

}
