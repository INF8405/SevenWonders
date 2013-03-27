package ca.polymtl.inf8405.sevenwonders
package app

import org.apache.thrift._
import transport.TServerSocket
import server.{TServer, TThreadPoolServer}
import akka.actor.{TypedProps, TypedActor, ActorSystem}

object Server extends App {

  new ServerImpl().start()
}

class ServerImpl {

  private var system: Option[ActorSystem] = _
  private var server: Option[TServer] = _

  def start(){

    system = Some( ActorSystem() )

    val lobby = TypedActor( system ).typedActorOf( TypedProps( classOf[GameLobby], new GameLobbyImpl( system ) ) )
    val dispatcher = TypedActor( system ).typedActorOf( TypedProps( classOf[Dispatcher], new DispatcherImpl( system, lobby ) ) )

    val serverTransport = new TServerSocket( 8001 )
    val serverArgs = new TThreadPoolServer.Args( serverTransport )
    serverArgs.processorFactory( new MessageProcessorFactory( dispatcher ) )

    server = Some( new TThreadPoolServer( serverArgs ) )

    server.foreach( _.serve() )
  }

  def stop(){
    server.foreach( _.stop() )
    system.foreach( _.shutdown() )
  }
}
