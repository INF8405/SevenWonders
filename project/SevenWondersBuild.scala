import sbt._
import sbt.Keys._

object SevenWondersBuild extends Build {

  lazy val main = Project(
    id = "main",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "7 Wonders",
      organization := "ca.polymtl.inf8405",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.0",
      libraryDependencies ++= Seq(
	"org.specs2" %% "specs2" % "1.14" % "test",
        "com.sidewayscoding" % "multisets_2.10" % "0.1"
      ),
      scalacOptions in Compile := Seq( "-feature" )
    )
  )
}
