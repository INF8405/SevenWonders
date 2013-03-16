name := "multisets"

version := "0.1"

organization := "com.sidewayscoding"

scalaVersion := "2.10.0"

resolvers ++= Seq(
  "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
)

scalacOptions ++= Seq(
  "-deprecation"
)

libraryDependencies ++= Seq( 
  "org.specs2" %% "specs2" % "1.14" % "test"
)

initialCommands := """
  import com.sidewayscoding._
  import com.sidewayscoding.immutable._"""
