import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, DateTime => _, _}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import monix.eval.Task
import monix.execution.{Cancelable, Scheduler}
import monix.reactive.OverflowStrategy.Unbounded
import monix.reactive._
import monix.reactive.subjects.ConcurrentSubject

import scala.concurrent.duration._
import scala.util.{Failure, Random, Success}


class Handler extends Actor with ActorLogging {
  import Handler._

  import io.circe.syntax._

  implicit val system: ActorSystem = context.system
  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  // Monix scheduler
  implicit val scheduler = Scheduler(context.dispatcher)

  // unbounded buffer for the user requests
  val ticksSubject: ConcurrentSubject[Tick, Tick] = ConcurrentSubject.publish[Tick](Unbounded)

  // for every Tick, simulate making a lot of requests to an endpoint
  val dataSource: Observable[(HttpRequest, RequestContext)] = ticksSubject.concatMap { tickRequest =>
    // simulate loading a day worth of time series from db
    val timeSeries: List[DataPoint] = List.fill(1000)(
      DataPoint(
        tickRequest.day.plusMillis(Random.nextInt(1000)),
        Random.nextDouble()
      )
    )

    // create the requests
    var i = -1
    val ingestionRequests = timeSeries.map { batch =>
      i += 1
      HttpRequest(
        method = HttpMethods.POST,
        uri = Uri(AppConfig.exampleEndpoint),
        entity = HttpEntity(
          ContentTypes.`application/json`,
          batch.asJson.toString.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        )
      ) -> RequestContext(requestNo = i, timeSeries.size)
    }

    Observable.fromIterable(ingestionRequests)
  }

  private var cancelable: Cancelable = Cancelable()
  override def preStart(): Unit = {
    val maxOpenRequests = 32 // e.g. "akka.http.host-connection-pool.max-open-requests"

    // limit the number of requests sent to 32 at all times
    val dataIngestionResponses = dataSource.mapAsync(parallelism = maxOpenRequests)(createRequestFn)
    val hotObservable = dataIngestionResponses.publish

    cancelable = hotObservable.connect()
    // hotObservable.subscribe(???) if needed. In this case we are just logging (Task[Unit])
  }

  override def postStop(): Unit = {
    cancelable.cancel()
  }

  override def receive = {
    case userRequest@Tick(_) =>
      ticksSubject.onNext(userRequest).onComplete(_ => ())
  }

  private def retryBackoff[A](
    source: Task[A],
    maxRetries: Int,
    firstDelay: FiniteDuration,
    context: RequestContext): Task[A] = {

    source.onErrorHandleWith {
      case ex: Exception =>
        if (maxRetries > 0) {
          log.info(s"$context: $ex: Retrying (${maxRetries - 1})... ")
          retryBackoff(source, maxRetries - 1, firstDelay * 2, context)
            .delayExecution(firstDelay)
        }
        else
          Task.raiseError(ex)
    }
  }

  private val createRequestFn = (item: (HttpRequest, RequestContext)) => {
    val task = Task.deferFuture(Http().singleRequest(item._1))
    val taskWithExponentialBackoff = retryBackoff(task, 5, 2.seconds, item._2)
    taskWithExponentialBackoff.materialize.map {
      case Success(response) =>
        if (response.status.intValue / 100 != 2 /*e.g. StatusCodes.Created*/)
          log.warning(s"${item._2}: " + response.toString)
        else if (item._2.requestNo == item._2.totalNumberOfRequests - 1)
          log.info(s"Done sending data (100%)...; Last one: ${item._2}")
        else if (item._2.requestNo % (item._2.totalNumberOfRequests / 20) == 0) { // log every 5% progress
          val progressPercentage = item._2.requestNo * 100 / (item._2.totalNumberOfRequests - 1)
          log.info(s"Sending data ($progressPercentage%)...; Current one: ${item._2}")
        }
        else
          ()
        response.discardEntityBytes()
        ()

      case Failure(ex) => log.warning(s"Err: $ex")
    }
  }
}

object Handler {
  implicit val dataPointEncoder: io.circe.Encoder[DataPoint] = io.circe.Encoder
    .forProduct2("timestamp", "value")(dp => (dp.timestamp.toString, dp.value.toString))

  case class Tick(day: org.joda.time.DateTime)
  case class RequestContext(requestNo: Int, totalNumberOfRequests: Int)
  case class DataPoint(timestamp: org.joda.time.DateTime, value: Double)
}