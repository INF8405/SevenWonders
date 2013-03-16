import sbt._
import Keys._

object Dependencies
{
	lazy val thrift   = "org.apache.thrift"    % "libthrift"          % "0.9.0"
	lazy val logback  = "ch.qos.logback"       % "logback-classic"    % "1.0.6"     % "runtime"
	lazy val actors   = "org.scala-lang"       % "scala-actors"       % "2.10.0"
	lazy val specs2 	= "org.specs2" 					%% "specs2" 						% "1.14" 			% "test"
	lazy val multiset =	"com.sidewayscoding" 	 % "multisets_2.10" 		% "0.1"
}