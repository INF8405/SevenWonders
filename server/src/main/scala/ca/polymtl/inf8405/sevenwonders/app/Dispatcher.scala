package ca.polymtl.inf8405.sevenwonders
package app

import api.{GeoLocation, GameRoomDef, SevenWondersApi}

import akka.actor._
import org.apache.thrift.transport.{TSocket, TTransport}

import scala.concurrent.duration._
import scala.concurrent.Future
import org.apache.thrift.TException

trait Dispatcher {
  protected val system: ActorSystem
  protected val lobby: GameLobby
  def create( game: GameRoomDef, player: GameClient ): Future[TGame]
  def created(): Unit
  def pong( transport: TTransport ): Unit
  def disconnect( transport: TTransport ): Unit
  def getOrAddProcessor( transport: TTransport ): SevenWondersApi.Processor[GameClient]
}

class DispatcherImpl(
  override val system: ActorSystem,
  override val lobby: GameLobby ) extends Dispatcher {

  def pong( transport: TTransport ) {
    clients get( transport ) foreach( _.alive = true )
  }

  def disconnect( transport: TTransport ) {
    clients.get( transport ).foreach( _.client.disconnect() )
    clients.remove( transport )
  }

  def create( game: GameRoomDef, player: GameClient ): Future[TGame] = {
    lobby.create( game, player, TypedActor.self )
  }

  def created() {
    clients foreach { case ( _, c ) => {
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

    if ( clients.contains( transport ) ) {
      clients( transport ).processor
    } else {
      val me = TypedActor.self[Dispatcher]
      val client: GameClient = TypedActor( system ).typedActorOf(TypedProps(
        classOf[GameClient],
        new GameClientImpl( transport, ip, lobby, me, system )
      ))

      val processor = new SevenWondersApi.Processor( client )
      clients( transport ) = new InnerClient( client, processor )

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

  private val clients = collection.mutable.Map.empty[ TTransport, InnerClient ]
}