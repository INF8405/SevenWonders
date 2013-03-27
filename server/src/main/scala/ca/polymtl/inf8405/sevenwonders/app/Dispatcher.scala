package ca.polymtl.inf8405.sevenwonders.app

import akka.actor._
import ca.polymtl.inf8405.sevenwonders.api.SevenWondersApi
import java.net.InetAddress
import org.apache.thrift.transport.{TSocket, TTransport}

import scala.concurrent.duration._

case object Ping
case class Pong( ip: InetAddress )
case class Disconnect( ip: InetAddress )
case class ProcessorRequest( transport: TTransport )

trait Dispatcher {
  protected val system: ActorSystem
  protected val lobby: GameLobby
  def pong( ip: InetAddress ): Unit
  def disconnect( ip: InetAddress ): Unit
  def getOrAddProcessor( transport: TTransport ): SevenWondersApi.Processor[GameClient]
}

class DispatcherImpl(
  override val system: ActorSystem,
  override val lobby: GameLobby ) extends Dispatcher {

  def pong( ip: InetAddress ) {
    clients get( ip ) foreach( _.alive = true )
  }

  def disconnect( ip: InetAddress ) {
    clients.get( ip ).foreach( _.client.disconnect() )
    clients.remove( ip )
  }

  /* This is kind of a thrift hack
   * if we receive a ip we instanciate a thrift client
   * so we can communicate in both directions
   */

  def getOrAddProcessor( transport: TTransport ) = {
    val socket = transport.asInstanceOf[TSocket]
    val ip = socket.getSocket.getInetAddress

    if ( clients.contains( ip ) ) {
      clients( ip ).processor
    } else {

      val client = TypedActor( system ).typedActorOf(TypedProps(
        classOf[GameClient],
        new GameClientImpl( transport, ip, lobby )
      ))

      val processor = new SevenWondersApi.Processor( client )
      clients( ip ) = new InnerClient( client, processor )

      processor
    }
  }

  /* Connection monitoring */

  val delta = 5.seconds
  import system.dispatcher

  system.scheduler.schedule( delta, delta ){
    clients map{ case ( ip, c ) => {
      if( c.alive ) {
        c.alive = false
        c.client.c_ping()
      } else {
        disconnect( ip )
      }
    }}
  }

  class InnerClient(
    val client: GameClient,
    val processor: SevenWondersApi.Processor[GameClient],
    var alive: Boolean = true
  )

  private val clients = collection.mutable.Map.empty[ InetAddress, InnerClient ]
}