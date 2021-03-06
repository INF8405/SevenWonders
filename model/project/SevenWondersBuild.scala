import sbt._
import sbt.Keys._

object SevenWondersBuild extends Build {

  lazy val main = Project(
    id = "main",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "sevenwonders-model",
      organization := "ca.polymtl.inf8405",
      version := "0.1.0",
      scalaVersion := "2.10.1",
      libraryDependencies ++= Seq(
        "org.specs2" %% "specs2" % "1.14" % "test"
      ),
      scalacOptions in Compile := Seq( "-feature" )
    )
  )
}
