import sbt._
import sbt.Keys._

object SevenWondersBuild extends Build {

  lazy val main = Project(
    id = "main",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "7 Wonders",
      organization := "com.github.jedesah"
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.0",
      libraryDependencies ++= Seq(
        "org.scalatest" % "scalatest_2.10" % "2.0.M5b" % "test"
      ),
      scalacOptions in Compile := Seq( "-feature" )
    )
  )
}
