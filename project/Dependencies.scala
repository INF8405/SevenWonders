import sbt._
import Keys._

object Dependencies
{
	lazy val thrift   		= "org.apache.thrift"    % "libthrift"          % "0.9.0"
	lazy val logback  		= "ch.qos.logback"       % "logback-classic"    % "1.0.6"   % "runtime"
	lazy val akka   		= "com.typesafe.akka" 	%% "akka-actor" 		% "2.1.0"
	lazy val akkaTest 		= "com.typesafe.akka" 	%% "akka-testkit" 		% "2.1.0" 	% "test"
	lazy val scalaTest		= "org.scalatest" 		%% "scalatest" 			% "2.0.M5b" % "test"
	lazy val model			= "ca.polymtl.inf8405"	%% "sevenwonders-model"	% "0.1.0"
	lazy val androidSupport = "com.google.android" 	 % "support-v4" % "r7"
}