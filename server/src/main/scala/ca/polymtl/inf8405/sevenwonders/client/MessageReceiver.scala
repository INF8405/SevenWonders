package ca.polymtl.inf8405.sevenwonders
package client

import api.HelloService

import org.apache.thrift.protocol.TProtocol
import actors.Actor
import annotation.tailrec

case class NewMessage( msg: String )

class MessageReceiver( protocol: TProtocol )
  extends Actor
  with HelloService.Iface
{
  val processor = new HelloService.Processor( this )

   def act()
  {
    loop
    {
      if( processor.process( protocol, protocol ) )
      {
        react {
          case NewMessage( msg ) =>
          {
            println( msg )
          }
        }
      }
    }
  }

  def c_client(msg: String)
  {
    this ! NewMessage( msg )
  }

  def s_server(msg: String) { /* Server side */}
}
