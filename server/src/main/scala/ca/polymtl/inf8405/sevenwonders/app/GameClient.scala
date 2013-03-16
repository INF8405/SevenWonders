package ca.polymtl.inf8405.sevenwonders
package app

import api.{HelloMsg, HelloService}

import org.apache.thrift.transport.TTransport
import api.HelloService.Client
import org.apache.thrift.protocol.TBinaryProtocol

class GameClient( transport: TTransport ) extends HelloService.Iface
{
  val client = new Client( new TBinaryProtocol( transport ) )

  def s_server( msg: String )
  {
    println( s"hey $msg" )
    client.c_client( msg )
  }

  // Client
  def c_client( msg: String ){}

}
