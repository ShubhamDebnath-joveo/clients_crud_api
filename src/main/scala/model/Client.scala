package model

import org.mongodb.scala.bson.ObjectId

import scala.language.postfixOps

object Client {
  private var currentId = 0
//  def apply(name: String, inboundFeedUrl: String): Client = {
//    currentId += 1
//    Client(new ObjectId, currentId, name, inboundFeedUrl)
//  }

  def apply(name: String, inboundFeedUrl: String, jobGroups: List[JobGroup] = List[JobGroup]()): Client = {
    currentId += 1
    Client(new ObjectId, currentId, name, inboundFeedUrl, jobGroups)
  }
}

case class Client(_id: ObjectId, clientId: Int, name: String, inboundFeedUrl: String, jobGroups: List[JobGroup])