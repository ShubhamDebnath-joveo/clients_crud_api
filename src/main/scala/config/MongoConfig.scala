package config

import model.{Client, Job, JobGroup, Publisher}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros
import org.mongodb.scala.{MongoClient, MongoDatabase}

object MongoConfig extends App {


  def getConnection: MongoDatabase = {
    val codecRegistry = fromRegistries(
      fromProviders(
        Macros.createCodecProvider[Client](),
        Macros.createCodecProvider[JobGroup](),
        Macros.createCodecProvider[Job](),
        Macros.createCodecProvider[Publisher]()),
      DEFAULT_CODEC_REGISTRY)
    val mongoClient: MongoClient = MongoClient()
    mongoClient.getDatabase("clients_db").withCodecRegistry(codecRegistry)
  }

  //  val client = model.Client("Rob", "Wick")
  //  val observable = collection.insertOne(client)
  //
  //  observable.toFuture().onComplete{
  //    case Success(value) => println(value.toString)
  //    case Failure(exception) => println(exception.getMessage)
  //  }
  //
  //  Thread.sleep(10000)
}
