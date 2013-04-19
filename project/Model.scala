import sbt._
import Keys._

object Model extends Build {

	import Dependencies._

	lazy val model = Project(
		id = "Model",
		base = file("model"),
		settings = Project.defaultSettings ++ Seq(
			name := "sevenwonders-model",
			organization := "ca.polymtl.inf8405",
			version := "0.1.0",
			scalaVersion := "2.10.1",
			libraryDependencies ++= Seq( spec2 ),
			scalacOptions in Compile := Seq( "-feature" )
		)
	)
}
