package ca.polymtl.inf8405.sevenwonders
package app

import org.apache.thrift._
import transport.TServerSocket
import server.TThreadPoolServer

object Server extends App
{
  private def start()
  {
    val serverTransport = new TServerSocket( 8001 )
    val serverArgs = new TThreadPoolServer.Args( serverTransport )

    serverArgs.processorFactory( new MessageProcessorFactory() )

    val server = new TThreadPoolServer( serverArgs )

    server.serve()
  }

  start()
}
