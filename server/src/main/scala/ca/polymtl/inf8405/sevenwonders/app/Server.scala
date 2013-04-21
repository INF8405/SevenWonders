package ca.polymtl.inf8405.sevenwonders
package app

import api.Config

import org.apache.thrift._
import transport.TServerSocket
import server.TThreadPoolServer
import akka.actor.{TypedProps, TypedActor, ActorSystem}

object Server extends App {

  new ServerImpl( ActorSystem() ).start()
}

class ServerImpl( system: ActorSystem ) {

  val lobby: Lobby = TypedActor( system ).typedActorOf( TypedProps( classOf[Lobby], new LobbyImpl( system ) ) )
  val dispatcher: Dispatcher = TypedActor( system ).typedActorOf( TypedProps( classOf[Dispatcher], new DispatcherImpl( system, lobby ) ) )

  val serverTransport = new TServerSocket( Config.port )
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