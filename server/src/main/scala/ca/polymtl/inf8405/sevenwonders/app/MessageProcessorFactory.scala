package ca.polymtl.inf8405.sevenwonders
package app

import api.HelloService

import org.apache.thrift._
import transport.{TSocket, TTransport}

import java.net.InetAddress

import collection.mutable.{Map => MMap}

class MessageProcessorFactory extends TProcessorFactory( null )
{
  private val clients = MMap.empty[InetAddress, GameClient]

  override def getProcessor( transport: TTransport ) =
  {
    val socket = transport.asInstanceOf[TSocket]
    val ip = socket.getSocket.getInetAddress

    // reuse client if exist
    val client = if ( clients.contains( ip ) ) clients( ip )
    else
    {
      val newClient = new GameClient( transport )
      clients( ip ) = newClient

      newClient
    }

    new HelloService.Processor( client )
  }
}
