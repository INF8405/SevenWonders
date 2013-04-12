package ca.polymtl.inf8405.sevenwonders.app

import akka.actor._
import ca.polymtl.inf8405.sevenwonders.api.{GeoLocation, GameRoomDef, SevenWondersApi}
import java.net.InetAddress
import org.apache.thrift.transport.{TSocket, TTransport}

import scala.concurrent.duration._
import scala.concurrent.Future
import org.apache.thrift.TException

trait Dispatcher {
  protected val system: ActorSystem
  protected val lobby: GameLobby
  def create( game: GameRoomDef, player: GameClient ): Future[TGame]
  def created(): Unit
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

  def create( game: GameRoomDef, player: GameClient ): Future[TGame] = {
    lobby.create( game, player, TypedActor.self )
  }

  def created() {
    clients foreach { case ( _, c ) => {
      println("create request")
      c.client.s_listGamesRequest( new GeoLocation(0,0) )
    }}
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

      println( "new connection " + ip )

      val me = TypedActor.self[Dispatcher]
      val client: GameClient = TypedActor( system ).typedActorOf(TypedProps(
        classOf[GameClient],
        new GameClientImpl( transport, ip, lobby, me, system )
      ))

      val processor = new SevenWondersApi.Processor( client )
      clients( ip ) = new InnerClient( client, processor )

      processor
    }
  }

  /* Connection monitoring */

  val delta = 500.milliseconds
  import system.dispatcher

  system.scheduler.schedule( delta, delta ){
    clients foreach { case ( ip, c ) => {
      if( c.alive ) {
        c.alive = false
        try{
          c.client.c_ping()
        } catch {
          case e: TException => disconnect( ip )
        }
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