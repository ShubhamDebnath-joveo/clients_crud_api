package model

case class ClientRequest(name: String, inboundFeedUrl: String, jobGroups: List[JobGroup]){}