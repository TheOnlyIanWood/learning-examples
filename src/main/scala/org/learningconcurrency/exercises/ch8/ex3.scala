package org.learningconcurrency.exercises.ch8

import akka.event.Logging
import org.learningconcurrency.ch8._

import akka.actor.Actor.Receive
import akka.actor.{Props, ActorRef, Actor}
import org.learningconcurrency.exercises.ch8.SessionActor.{EndSession, StartSession}


object SessionActor {
  case class StartSession(password: String)
  case object EndSession
  def props(password: String, r: ActorRef) = Props(new SessionActor(password,r))
}

class MassiveActor extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case x => log.info(x.toString)
  }
}


class SessionActor(password: String, r: ActorRef) extends Actor {


  def receive: Receive = stopped

  def stopped: Receive = {
    case StartSession(p) if (p == password) => context become going
  }

  def going: Receive = {
    case EndSession => context.stop(self)
    case x => r forward x
  }
}

object Session extends  App{

  val r = ourSystem.actorOf(Props[MassiveActor])
  val sessionActor = ourSystem.actorOf(SessionActor.props("pass123", r))

  sessionActor ! "1"
  sessionActor ! StartSession("arse")
  sessionActor ! "2"
  sessionActor ! StartSession("pass123")
  sessionActor ! "3"
  sessionActor ! EndSession
  sessionActor ! "4"

Thread.sleep(1000)
}