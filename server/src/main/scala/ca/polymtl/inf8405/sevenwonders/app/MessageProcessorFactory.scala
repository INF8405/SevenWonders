package ca.polymtl.inf8405.sevenwonders.app

import org.apache.thrift.TProcessorFactory
import org.apache.thrift.transport.TTransport
import akka.actor.ActorRef
import concurrent.Await
import scala.concurrent.duration._

import ca.polymtl.inf8405.sevenwonders.api.SevenWondersApi
import akka.util.Timeout


class MessageProcessorFactory( dispatcher: ActorRef ) extends TProcessorFactory( null ) {

  override def getProcessor( transport: TTransport ) = {

    import akka.pattern.ask
    implicit val timeout = Timeout( 5 seconds )

    val future = dispatcher ? ProcessorRequest( transport )
    Await.result( future, timeout.duration ).asInstanceOf[SevenWondersApi.Processor[GameClient]]
  }
}
