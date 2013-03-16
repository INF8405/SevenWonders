import sbt._
import Keys._

object Model extends Build
{
	import Dependencies._

	lazy val model = Project(
    id = "Model",
    base = file("model"),
    settings = Project.defaultSettings ++ Seq(
      organization := Settings.org,
      version := Settings.version ,
      scalaVersion := Settings.scalaVersion,
      libraryDependencies ++= Seq(
				specs2,
				multiset			
      ),
      scalacOptions in Compile := Settings.scalacOptions
    )
  )
}