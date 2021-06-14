import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import config.MongoConfig
import controller.ClientsAPI
import model.Client
import org.mongodb.scala.MongoCollection
import repo.ClientRepository
import service.JobService

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object MainApp extends App{

  implicit val system: ActorSystem = ActorSystem("web-app")
  private implicit val dispatcher: ExecutionContextExecutor = system.dispatcher
  private implicit val materializer = ActorMaterializer

  val database = MongoConfig.getConnection
  val clientCollection: MongoCollection[Client] = database.getCollection("client_coll_1")

//  val clients: Seq[model.Client] = Seq(
//    model.Client("client1", "xyz.com"),
//      model.Client("client2", "xyz.com"),
//    model.Client("client3", "xyz.com"),
//    model.Client("client4", "xyz.com")
//  )
//
//  Await.result(repository.insertMany(clients).toFuture(), 2.seconds)

  val routes = new ClientsAPI(ClientRepository(clientCollection), JobService()).getRoutes
  val serverFuture = Http().bindAndHandle(routes, "localhost", 8080)
  println(s"Server online at http://localhost:8080/\n Press RETURN to stop...")
  StdIn.readLine()

  serverFuture.flatMap(_.unbind())
    .onComplete(- => system.terminate())
}


