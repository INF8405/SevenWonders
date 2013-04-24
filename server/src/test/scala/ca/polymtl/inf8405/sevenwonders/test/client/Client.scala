package ca.polymtl.inf8405.sevenwonders
package test.client

import api.{Config, SevenWondersApi}

import akka.actor.{ActorRef, ActorSystem}

import org.apache.thrift._
import transport.TSocket
import protocol.TBinaryProtocol

class Client( system: ActorSystem, probe: ActorRef, name: String, ignorePing: Boolean = true ) {

  val protocolFactory = new TBinaryProtocol.Factory
  val transport = new TSocket("localhost", Config.port)
  val protocol = new TBinaryProtocol(transport)

  val sender = new SevenWondersApi.Client(protocol)

  val receiver = new ReceiverImpl( sender, protocol, system, probe, name, ignorePing )

  transport.open()
  receiver.start()

  def stop() {
    receiver.stop()
  }
}