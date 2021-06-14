package service

import config.JsonHelper
import model.{Client, Job, JobGroup, Publisher}
import org.json4s.native.JsonMethods
import org.json4s.native.Serialization.writePretty
import rules.Rule

import java.io.{BufferedWriter, File, FileWriter}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class JobService() extends JsonHelper {

  def createOutboundFeeds(clientsFuture: Future[Seq[Client]]): Future[String] = {
    clientsFuture.map(clients => {
      clients.foreach(createOutboundFeed)
      "Successfully generated outbound feeds"
    })
  }

  def createOutboundFeed(client: Client): Unit = {
    val jobs: List[Job] = getEntity[List[Job]](client.inboundFeedUrl)
    val jobIdMap: Map[String, Job] = jobs.map(job => (job.jobId, job)).toMap
    val publisherIdMap: Map[String, Publisher] = client.jobGroups.flatMap(_.sponsoredPublishers).map(publisher => publisher.publisherId -> publisher).toMap

    val jobToGroupMap = client.jobGroups.flatMap(jobGroup => {
      val rule: Rule = parse(jobGroup.rules).extract[Rule]
      jobs.filter(rule.evaluate).map(_.jobId -> jobGroup)
    }).groupBy { case (a: String, b: JobGroup) => a }
      .map {
        case (a: String, b: List[(String, JobGroup)]) =>
          a -> b.map(_._2)
            .reduce((j1: JobGroup, j2: JobGroup) =>
              if (replaceJobGroup(j1, j2) < 0) j1 else j2)
      }


    val publisherToJobMap = jobToGroupMap.toSeq
      .flatMap { case (jobId: String, group: JobGroup) => group.sponsoredPublishers
        .map(_.publisherId).map(_ -> jobId)
      }
      .groupBy(_._1)
      .map { case (pubId: String, b: Seq[(String, String)]) => pubId -> b.map(_._2) }

    publisherToJobMap.foreach {
      case (pubId: String, jobIds: Seq[String]) =>
        writeJSONToFile[Seq[Job]](
          jobIds.map(jobIdMap(_)),
          publisherIdMap(pubId).outBoundFileName)
    }

    println(jobToGroupMap)
    println(publisherToJobMap)
  }

  def getEntity[T](path: String)(implicit m: Manifest[T]): T = {
    val entitySrc = scala.io.Source.fromFile(path)
    val entityStr = try entitySrc.mkString finally entitySrc.close()
    JsonMethods.parse(entityStr).extract[T]
  }

  def writeJSONToFile[T](entity: T, filePath: String): Unit = {
    val bw = new BufferedWriter(new FileWriter(new File(filePath), false))
    bw.write(writePretty(entity))
    bw.close()
  }

  def replaceJobGroup(jobGroup1: JobGroup, jobGroup2: JobGroup): Int = {
    if (jobGroup2.priority == jobGroup1.priority) {
      jobGroup2.createdDate.compareTo(jobGroup1.createdDate)
    }
    else {
      jobGroup1.priority.compareTo(jobGroup2.priority)
    }
  }

}
