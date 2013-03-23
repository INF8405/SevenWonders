package ca.polymtl.inf8405.sevenwonders
package app

import org.apache.thrift._
import transport.TServerSocket
import server.TThreadPoolServer
import akka.actor.{Props, ActorSystem}

object Server extends App {

  private def start() {

    val system = ActorSystem()

    val dispatcher = system.actorOf( Props[Dispatcher] )

    val serverTransport = new TServerSocket( 8001 )
    val serverArgs = new TThreadPoolServer.Args( serverTransport )

    serverArgs.processorFactory( new MessageProcessorFactory( dispatcher ) )

    val server = new TThreadPoolServer( serverArgs )

    server.serve()
  }

  start()
}
