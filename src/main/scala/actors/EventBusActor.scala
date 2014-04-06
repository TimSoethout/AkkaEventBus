package actors

import akka.actor.Actor
import eventbus.MainEventBus
import actors.EventBusActor.{EventBusReply, GetEventBus, Subscribe}

object EventBusActor {

  case class Subscribe(channel: String)

  case object GetEventBus

  case class EventBusReply(eventBus: MainEventBus)

}

class EventBusActor extends Actor {

  val appActorEventBus = new MainEventBus

  def receive = {
    case Subscribe(channel) => appActorEventBus.subscribe(sender, channel)
    case GetEventBus => sender ! EventBusReply(appActorEventBus)
  }
}

