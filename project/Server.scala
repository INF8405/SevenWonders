import sbt._
import Keys._

object Server extends Build {

  import Dependencies._
  import Model._
  import com.github.bigtoast.sbtthrift.ThriftPlugin

  lazy val server = Project(
    id = "Server",
    base = file("server"),
    settings = Project.defaultSettings ++ Seq(
      name := "Server",
      organization := Settings.org,
      version := Settings.version,
      scalaVersion := Settings.scalaVersion,
      resolvers ++= Settings.resolvers,
      libraryDependencies ++= Seq( 
        akka, 
        logback,
        scalaTest,
        akkaTest
      ),
      parallelExecution in Test := false
    )
  ) dependsOn ( model , api )

  lazy val api = Project(
    id = "Api",
    base = file("api"),
    settings = Project.defaultSettings ++ ThriftPlugin.thriftSettings ++ Seq(
      name := "Api",
      organization := Settings.org,
      version := "0.1-SNAPSHOT",
      scalaVersion := Settings.scalaVersion,
      resolvers ++= Settings.resolvers,
      libraryDependencies += thrift
    )
  )
}