import actors.EventBusActor.EventBusReply
import akka.actor._
import akka.testkit.ImplicitSender
import eventbus.EventBus._
import eventbus.EventBus.MessageEvent
import eventbus.MainEventBus
import org.joda.time.DateTime
import org.scalatest._
import scala.concurrent.Await
import scala.util.Random
import akka.testkit.TestKit

//import com.github.nscala_time.time.Imports._

import actors._
import scala.concurrent.duration._
import akka.pattern._
import akka.util.Timeout

class EventBusSpec(_system: ActorSystem) extends TestKit(_system)
with ImplicitSender
with WordSpecLike
with Matchers
with Inside
with BeforeAndAfterAll {

  def this() = this(ActorSystem("EventBusSpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(1000)

  "A login message" must {
    "be pushed to the login eventbus" in {
      val eventBusActor = system.actorOf(Props(classOf[EventBusActor]))
      // get the event bus
      val eventBus: MainEventBus = Await.result((eventBusActor ? EventBusActor.GetEventBus).mapTo[EventBusReply], timeout.duration).eventBus

      var events: List[MessageEvent] = Nil

      // Output event bus to console
      val subscriber = system.actorOf(Props(new Actor {

        import akka.actor._

        def receive = {
          case d: MessageEvent => events = d :: events
        }

        eventBusActor ! EventBusActor.Subscribe(LOGIN_CHANNEL)
      }))

      system.actorOf(Props(new User(eventBus, "123"))) ! User.Login

      awaitAssert {
        events should have length 1
        inside(events.head) {
          case MessageEvent(channel, message) =>
            channel should be(LOGIN_CHANNEL)
            message should have('userId("123"))
        }
      }

    }
  }

  "Each login" must {
    "be counted in the event stream" in {
      val eventBusActor = system.actorOf(Props(classOf[EventBusActor]))
      // get the event bus
      val eventBus: MainEventBus = Await.result((eventBusActor ? EventBusActor.GetEventBus).mapTo[EventBusReply], timeout.duration).eventBus
      var count = 0;
      val subscriber = system.actorOf(Props(new Actor {
        def receive = {
          case d: MessageEvent => count += 1
        }

        eventBusActor ! EventBusActor.Subscribe(LOGIN_CHANNEL)
      }))

      val users = 1 to 100 map {
        i =>
          system.actorOf(Props(new User(eventBus, i.toString)))
      }

      var loginCount = 0

      1 to 100 foreach {
        _ =>
          users foreach {
            user =>
              val wait = new Random(DateTime.now.getMillis).nextInt(1000).milliseconds
              system.scheduler.scheduleOnce(wait) {
                user ! User.Login
                loginCount += 1
              }
          }
      }

      // TODO: use a more idiomatic approach to wait and test if all events are logged
      Thread.sleep(1100)
//      within(0.second, 1.2.second)(
        loginCount should be(count)
    }
  }
}