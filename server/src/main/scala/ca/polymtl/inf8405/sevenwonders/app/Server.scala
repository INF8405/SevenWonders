package ca.polymtl.inf8405.sevenwonders
package app

import org.apache.thrift._
import transport.TServerSocket
import server.TThreadPoolServer
import akka.actor.{TypedProps, TypedActor, ActorSystem}

object Server extends App {

  new ServerImpl( ActorSystem() ).start()
}

class ServerImpl( system: ActorSystem ) {

  val lobby: GameLobby = TypedActor( system ).typedActorOf( TypedProps( classOf[GameLobby], new GameLobbyImpl( system ) ) )
  val dispatcher: Dispatcher = TypedActor( system ).typedActorOf( TypedProps( classOf[Dispatcher], new DispatcherImpl( system, lobby ) ) )

  val serverTransport = new TServerSocket( 8001 )
  val serverArgs = new TThreadPoolServer.Args( serverTransport )
  serverArgs.processorFactory( new MessageProcessorFactory( dispatcher ) )
  val server = new TThreadPoolServer( serverArgs )

  def start() {
    server.serve()
  }

  def stop() {
    server.stop()
  }
}