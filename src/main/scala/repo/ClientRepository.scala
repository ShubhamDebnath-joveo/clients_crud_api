package repo

import model.{Client, JobGroup}
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.{combine, push, set}
import org.mongodb.scala.model.{Filters, FindOneAndUpdateOptions}
import org.mongodb.scala.result.InsertOneResult

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

case class ClientRepository(clientCollection: MongoCollection[Client]) {

  def getAllClients(pageSize: Int, pageNum: Int): Future[Seq[Client]] = {
    clientCollection.find().skip(pageSize * (pageNum - 1)).limit(pageSize).toFuture()
  }

  def getAllClients(): Future[Seq[Client]] = {
    clientCollection.find().toFuture()
  }

  def getClient(clientId: Int): Client = {
    Await.result(clientCollection.find(Filters.eq("clientId", clientId)).toFuture(), 2.seconds)(0)
  }

  def createClient(client: Client): Future[InsertOneResult] = clientCollection.insertOne(client).toFuture()

  def updateClient(clientId: Int, client: Client): Future[Client] = clientCollection.findOneAndUpdate(
    equal("clientId", clientId),
    setBsonValue(client),
    FindOneAndUpdateOptions().upsert(true))
    .toFuture()

  def addJobGroup(clientId: Int, jobGroup: JobGroup): Future[Client] = clientCollection.findOneAndUpdate(
    equal("clientId", clientId),
    push("jobGroups", jobGroup))
    .toFuture()


  private def setBsonValue(client: Client): Bson = {
    combine(
      set("name", client.name),
      set("inboundFeedUrl", client.inboundFeedUrl)
    )
  }

}
