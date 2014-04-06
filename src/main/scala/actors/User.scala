package actors

import akka.actor.Actor
import akka.event.LoggingReceive
import actors.User.Login
import eventbus.MainEventBus
import eventbus.EventBus._
import eventbus.EventBus.MessageEvent

object User {

  sealed trait UserAction

  case object Login
}


class User(eventBus: MainEventBus, userId: String) extends Actor {

  override def receive: Receive = LoggingReceive {
    case Login => {
      eventBus.publish(MessageEvent(LOGIN_CHANNEL, LoginMessage(userId = userId)))
    }
  }
}
