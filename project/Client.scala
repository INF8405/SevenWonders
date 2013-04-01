import sbt._

import Keys._
import AndroidKeys._

object AndroidBuild extends Build 
{
  import Dependencies._

  val allSettings = Defaults.defaultSettings ++ Seq(
    name := Settings.name,
    organization := Settings.org,
    version := Settings.version,
    versionCode := 0,
    scalaVersion := Settings.scalaVersion,
    platformName in Android := "android-13",
    libraryDependencies ++= Seq(logback, androidSupport)
  )

  lazy val fullAndroidSettings = (
      allSettings ++
      AndroidProject.androidSettings ++
      TypedResources.settings ++
      AndroidManifestGenerator.settings ++
      AndroidMarketPublish.settings ++ Seq(
        keyalias in Android := "change-me"
      ) ++ Seq(
        useProguard in Android := false
      )
   )

  lazy val base = "client"

  lazy val main = Project(
    "Client",
    file( base ),
    settings = fullAndroidSettings
  ) dependsOn Server.api

  lazy val tests = Project(
    "Client-Tests",
    file( base + "/tests" ),
    settings = allSettings ++ AndroidTest.androidSettings ++ Seq (
      name := "Client Tests"
    )
  ) dependsOn main
}
