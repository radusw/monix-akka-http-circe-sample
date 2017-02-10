import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}

import scala.io.StdIn
import scala.util.Try

object Main extends App {
  Try(args(0)).foreach(appConfigFile => System.setProperty("config.file", appConfigFile))
  if (!AppConfig.logToFile) System.setProperty("logback.configurationFile", "logback.stdout.xml")

  import system.dispatcher

  implicit val system: ActorSystem = ActorSystem("my-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  private val log = Logging(system, this.getClass)

  private val handler = system.actorOf(Props[Handler], "handler")

  val route = get {
    path("tick") {
      handler ! Handler.Tick(org.joda.time.DateTime.now())
      complete(s"Triggered...")
    }
  }

  val bindingFuture = Http().bindAndHandle(route, AppConfig.httpInterface, AppConfig.httpPort)

  log.info(s"Server up at ${AppConfig.httpInterface}:${AppConfig.httpPort}. Type `exit` to stop...")
  var continue = true
  while (continue) {
    val c = StdIn.readLine()
    continue = c != "exit"
  }

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}

object AppConfig {
  private val config: Config = ConfigFactory.load()
  private val httpConfig: Config = config.getConfig("http")
  val httpInterface: String = httpConfig.getString("interface")
  val httpPort: Int = httpConfig.getInt("port")
  val exampleEndpoint = config.getString("endpoint")
  val logToFile = config.getBoolean("log.to.file")
}
