package controller

import akka.http.scaladsl.server.Directives.{_symbol2NR, complete, entity, parameters, path, pathPrefix}
import akka.http.scaladsl.server.PathMatchers.Segment
import akka.http.scaladsl.server.directives.PathDirectives
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import config.JsonHelper
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
import model.{Client, ClientRequest, JobGroup, JobGroupRequest}
import repo.ClientRepository
import service.JobService

import scala.concurrent.ExecutionContext
import scala.language.postfixOps


class ClientsAPI(clientRepo: ClientRepository, jobService: JobService)(implicit ec: ExecutionContext, mat: Materializer) extends JsonHelper {

  val getRoutes: Route =
    PathDirectives.pathPrefix("api") {
      Directives.concat(
        path("clients") {
          Directives.get {
            parameters(Symbol("pageSize").as[Int], Symbol("pageNum").as[Int]) {
              (pageSize, pageNum) =>
                val clients = clientRepo.getAllClients(pageSize, pageNum)

                complete(clients)
            }
          }
        },

        path("client") {
          Directives.post {
            entity(Directives.as[String]) {
              clientReqJson =>
                val clientRequest = parse(clientReqJson).extract[ClientRequest]
                complete(clientRepo.createClient(Client(clientRequest.name, clientRequest.inboundFeedUrl, clientRequest.jobGroups)))

            }
          }
        },

        pathPrefix("client") {
          pathPrefix(Segment) { clientId =>
            Directives.concat(

              path("update") {
                Directives.put {
                  entity(Directives.as[String]) {
                    clientReqJson =>

                      val clientRequest = parse(clientReqJson).extract[ClientRequest]
                      complete(clientRepo.updateClient(clientId.toInt, Client(clientRequest.name, clientRequest.inboundFeedUrl, clientRequest.jobGroups)))

                  }
                }
              },

              path("jobGroup") {
                Directives.post {
                  entity(Directives.as[String]) {
                    jobGroupRequestJson =>
                      val jobGroupRequest = parse(jobGroupRequestJson).extract[JobGroupRequest]
                      complete(clientRepo.addJobGroup(clientId.toInt, JobGroup(rules = write(jobGroupRequest.rules),
                        sponsoredPublishers = jobGroupRequest.sponsoredPublishers,
                        priority = jobGroupRequest.priority)))
                  }
                }
              },
            )
          }
        },

        path("generateFeed") {
          Directives.get {
            complete(jobService.createOutboundFeeds(clientRepo.getAllClients()))
          }
        },
      )
    }
}
