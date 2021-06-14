//enablePlugins(JavaAppPackaging)
//enablePlugins(DockerPlugin)


name := "clients_crud_api"

version := "0.1"

scalaVersion := "2.12.13"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.6.10",
  "com.typesafe.akka" %% "akka-http" % "10.2.2",
  "org.json4s" %% "json4s-jackson" % "3.7.0-M7",
  "org.json4s" %% "json4s-native" % "3.7.0-M7",
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "org.mongodb.scala" %% "mongo-scala-driver" % "4.2.0",
  "de.heikoseeberger" %% "akka-http-json4s" % "1.26.0")