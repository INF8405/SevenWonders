package ca.polymtl.inf8405.sevenwonders.test.client

import akka.actor.{TypedProps, TypedActor, ActorSystem}
import org.apache.thrift.transport.{TNonblockingSocket, TSocket}
import org.apache.thrift.protocol.TBinaryProtocol
import ca.polymtl.inf8405.sevenwonders.api
import api.SevenWondersApi
import org.apache.thrift.async.TAsyncClientManager

class Client( system: ActorSystem, dispatcher: Mock ) {

  val protocolFactory = new TBinaryProtocol.Factory
  val transport = new TSocket("localhost", 8001)
  val protocol = new TBinaryProtocol(transport)

  val sender = new SevenWondersApi.Client(protocol)

  val receiver = new ReceiverImpl(
    sender,
    protocol,
    system,
    dispatcher
  )

  transport.open()
  receiver.start()

  def stop() {
    receiver.stop()
  }
}