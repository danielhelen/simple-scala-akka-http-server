import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object HTTPServer {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "simple-scala-akka-http-server")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val route =
      path("hello-world") {
        get {
          complete(
            HttpEntity(ContentTypes.`text/html(UTF-8)`,
              "<h1>Scala AKKA HTTP Server</h1><p>Hello World! This server uses Scala and the Akka HTTP modules.</p>"
            ))
        }
      }

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(s"The server is running.\nVisit http://localhost:8080/hello-world")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
