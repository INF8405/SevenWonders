import sbt._
import Keys._

object Settings
{
  val name = "Seven Wonders"
  val scalaVersion = "2.10.0"
  val version = "0.1-SNAPSHOT"
  val org = "ca.polymtl.inf8405"
  val resolvers = Seq(
    "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",
    "Typesafe" at "http://typesafe.artifactoryonline.com/typesafe/repo"
  )
  val scalacOptions = Seq( "-feature" )
}