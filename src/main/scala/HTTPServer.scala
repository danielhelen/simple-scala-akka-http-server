import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

final case class Record(id: Long, name: String)
final case class Records(items: List[Record])

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val recordFormat: RootJsonFormat[Record] = jsonFormat2(Record)
  implicit val recordsFormat: RootJsonFormat[Records] = jsonFormat1(Records)
}

object HTTPServer extends Directives with JsonSupport {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "simple-scala-akka-http-server")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val recordOne = Record(1, "this is record one")
    val recordTwo = Record(2, "this is record two")
    val records = List(recordOne, recordTwo)
    val jsonString = records.toJson.toString()

    val route =
      path("hello-world") {
        get {
          complete(HttpEntity(ContentTypes.`application/json`, jsonString))
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
