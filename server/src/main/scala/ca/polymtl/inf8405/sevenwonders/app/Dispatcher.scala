package ca.polymtl.inf8405.sevenwonders.app

import akka.actor.{ActorRef, Props, ActorSystem, Actor}
import ca.polymtl.inf8405.sevenwonders.api.SevenWondersApi
import java.net.InetAddress
import org.apache.thrift.transport.{TSocket, TTransport}

import scala.concurrent.duration._

case object Ping
case class Pong( ip: InetAddress )
case class Disconnect( ip: InetAddress )
case class ProcessorRequest( transport: TTransport )
class Dispatcher( system: ActorSystem ) extends Actor {

  def receive = {
    case Pong( ip ) => {
      clients get( ip ) foreach( _.alive = true )
    }
    case Disconnect( ip ) => {
      clients remove( ip )
    }
    case ProcessorRequest( transport ) => {
      sender ! getOrAddProcessor( transport )
    }
  }

  case class InnerClient(
    client: ActorRef,
    processor: SevenWondersApi.Processor[GameClient],
    var alive: Boolean = true
  )

  /* Connection monitoring */

  val delta = 5.seconds
  import system.dispatcher

  system.scheduler.schedule( delta, delta ){
    clients map{ case ( inet, c ) => {
      if( c.alive ) {
        c.alive = false
        c.client ! Ping
      } else {
        self ! Disconnect( inet )
      }
    }}
  }

  private def getOrAddProcessor( transport: TTransport ) = {
    val socket = transport.asInstanceOf[TSocket]
    val ip = socket.getSocket.getInetAddress

    if ( clients.contains( ip ) ) {
      clients( ip ).processor
    } else {

      val client = new GameClient( transport, ip )
      val clientActor = system.actorOf( Props( client ) )
      val processor = new SevenWondersApi.Processor( client )
      clients( ip ) = InnerClient( clientActor, processor )

      processor
    }
  }

  private val clients = collection.mutable.Map.empty[ InetAddress, InnerClient ]
}