package ca.polymtl.inf8405.sevenwonders
package app

import ca.polymtl.inf8405.sevenwonders.api._

import akka.actor._
import org.apache.thrift.transport.TTransport

import scala.concurrent.duration._
import org.apache.thrift.TException


trait Dispatcher {
  protected val system: ActorSystem
  protected val lobby: Lobby

  def pong( transport: TTransport ): Unit
  def disconnect( transport: TTransport ): Unit
  def getOrAddProcessor( transport: TTransport ): SevenWondersApi.Processor[Client]
}

class DispatcherImpl(
  override val system: ActorSystem,
  override val lobby: Lobby ) extends Dispatcher
{

  def pong( transport: TTransport ) {
    clients get( transport ) foreach( _.connected = true )
  }

  def disconnect( transport: TTransport ) {
    clients.get( transport ).foreach( _.api.disconnect() )
    clients.remove( transport )
  }

  /* This is kind of a thrift hack
   * if we receive a ip we instanciate a thrift client
   * so we can communicate in both directions
   */
  def getOrAddProcessor( transport: TTransport ) = {
    if ( clients.contains( transport ) ) {
      clients( transport ).processor
    } else {
      val me = TypedActor.self[Dispatcher]
      val api: Client = TypedActor( system ).typedActorOf(TypedProps(
        classOf[Client],
        new ClientImpl( transport, lobby, me, system )
      ))

      val processor = new SevenWondersApi.Processor( api )
      clients( transport ) = new InnerClient( api, transport, processor )

      processor
    }
  }

  /* Connection monitoring */

  val delta = 500.milliseconds
  import system.dispatcher

  system.scheduler.schedule( delta, delta ){
    clients foreach { case ( ip, client ) => {
      if( client.connected ) {
        client.connected = false
        try{
          client.api.c_ping()
        } catch {
          case e: TException => disconnect( ip )
        }
      } else {
        disconnect( ip )
      }
    }}
  }

  class InnerClient(
    val api: Client,
    transport: TTransport,
    val processor: SevenWondersApi.Processor[Client],
    var connected: Boolean = false
  )

  private val clients = collection.mutable.Map.empty[ TTransport, InnerClient ]
}