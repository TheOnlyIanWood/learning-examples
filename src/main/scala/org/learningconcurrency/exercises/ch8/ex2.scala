package org.learningconcurrency.exercises.ch8

import akka.actor.Actor.Receive
import akka.actor.{Props, Actor, ActorRef}
import akka.event.Logging
import org.learningconcurrency.ch2.SynchronizedNesting.Account
import org.learningconcurrency.exercises.ch8.AccountActor.{Send, Transfer}
import org.learningconcurrency.ch8._


/**
 * Created by ian on 17/07/15.
 */
object AccountActor {

  case class Send(target: ActorRef, amount: Int)
  case class Transfer(amount: Int)

}


class AccountActor extends Actor {
  val log = Logging(context.system, this)

  var balance = 0

  //TODO need to cater for Kill
  def receive: Receive = {
    case Send(target, amount) => {
      val initialBalance = balance
      balance -= amount
      log.info(s"sending $amount, balance was $initialBalance now $balance")
      target ! Transfer(amount)
    }
    case Transfer(amount) =>
      val initialBalance = balance
      balance += amount
      log.info(s"received $amount, balance now $balance was $initialBalance")
  }

}

object AccountActorTester extends App {

  val mary = ourSystem.actorOf(Props[AccountActor], "mary")
  val bob = ourSystem.actorOf(Props[AccountActor], "bob")

  mary ! Send(bob, 10)

  Thread.sleep(10000L)



}