import sbt._
import Keys._
import xml.XML

import AndroidKeys._

object AndroidBuild extends Build 
{
  import Dependencies._

  val googlePlayServices = SettingKey[File]("google-play-services-sdk", "Path to Google Play services SDK")

  val allSettings = Defaults.defaultSettings ++ Seq(
    name := "sevenwonders-client",
    organization := Settings.org,
    version := Settings.version,
    versionCode := 0,
    scalaVersion := Settings.scalaVersion,
    platformName in Android := "android-17",
    libraryDependencies ++= Seq(logback, androidSupport)
  )

  lazy val fullAndroidSettings = (
      allSettings ++
      AndroidProject.androidSettings ++
      TypedResources.settings ++
      AndroidManifestGenerator.settings ++
      AndroidMarketPublish.settings ++ Seq(
        keyalias in Android := "SevenWonders",
        useProguard in Android := false,

        // Get path to Google Play services SDK from path to Android SDK path.
        googlePlayServices <<= (sdkPath in Android) { path =>
          path / "extras" / "google" / "google_play_services" / "libproject" / "google-play-services_lib"
        },
        // Add Google Maps library project as dependency.
        extractApkLibDependencies in Android <+= googlePlayServices map { path =>
          LibraryProject(
            pkgName = "com.google.android.gms",
            manifest = path / "AndroidManifest.xml",
            sources = Set(),
            resDir = Some(path / "res"),
            assetsDir = None
          )
        },
        // Add Google Maps JAR.
        unmanagedJars in Compile <+= googlePlayServices map { path => Attributed.blank(path / "libs" / "google-play-services.jar") },
        // Protect some classes from filtering out by ProGuard.
        proguardOption in Android ~= { _ + " -keep class * extends java.util.ListResourceBundle { protected Object[][] getContents(); } " },
        proguardOption in Android ~= { _ + " -keep class com.google.android.gms.maps.SupportMapFragment { *; } " },
        proguardOption in Android ~= { _ + " -keep class com.google.android.gms.maps.MapFragment { *; } " }
      )
   )

  lazy val base = "client"

  lazy val main = Project(
    "Client",
    file( base ),
    settings = fullAndroidSettings
  ) dependsOn Server.api

  lazy val tests = Project(
    "ClientTests",
    file( base + "/tests" ),
    settings = allSettings ++ AndroidTest.androidSettings ++ Seq (
      name := "sevenwonders-client-tests"
    )
  ) dependsOn main
}
