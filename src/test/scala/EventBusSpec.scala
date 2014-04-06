import actors.EventBusActor.EventBusReply
import akka.actor._
import akka.testkit.ImplicitSender
import eventbus.EventBus._
import eventbus.MainEventBus
import org.scalatest._
import scala.concurrent.Await
import scala.util.Random
import akka.testkit.TestKit
import com.github.nscala_time.time.Imports._
import actors._
import scala.concurrent.duration._
import akka.pattern._
import akka.util.Timeout

class EventBusSpec(_system: ActorSystem) extends TestKit(_system)
with ImplicitSender
with WordSpecLike
with Matchers
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

      // Output event bus to console
      val subscriber = system.actorOf(Props(new Actor {

        import akka.actor._

        def receive = {
          case d: MessageEvent => println(d)
        }

        eventBusActor ! EventBusActor.Subscribe(LOGIN_CHANNEL)
      }))

      val users = 1 to 100 map {
        i =>
          system.actorOf(Props(new User(eventBus, i.toString)))
      }

      1 to 100 foreach {
        _ =>
          users foreach {
            user =>
              val wait = new Random(DateTime.now.getMillis).nextInt(1000).milliseconds
              system.scheduler.scheduleOnce(wait)(user ! User.Login)
          }
      }

    }

  }
}