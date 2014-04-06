package eventbus

import java.util.{Date, UUID}
import akka.event.{LookupClassification, ActorEventBus}
import eventbus.EventBus.MessageEvent

object EventBus {

  sealed class Message(val id: String, val timestamp: Long)

  case class LoginMessage(override val id: String = UUID.randomUUID().toString, override val timestamp: Long = new Date().getTime, userId: String) extends Message(id, timestamp)

  case class MessageEvent(channel: String, message: Message)

  val LOGIN_CHANNEL = "/login"
}

class MainEventBus extends ActorEventBus with LookupClassification {
  type Event = MessageEvent
  type Classifier = String


  protected def mapSize(): Int = {
    10
  }

  protected def classify(event: Event): Classifier = {
    event.channel
  }


  protected def publish(event: Event, subscriber: Subscriber): Unit = {
    subscriber ! event
  }
}