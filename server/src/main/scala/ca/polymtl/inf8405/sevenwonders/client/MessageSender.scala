package ca.polymtl.inf8405.sevenwonders
package client

import api._

import org.apache.thrift.protocol.TProtocol

import actors.Actor


class MessageSender( protocol: TProtocol ) extends Actor
{
  val client = new HelloService.Client( protocol )

  def act()
  {
    loop
    {
      react
      {
        case NewMessage( msg ) =>
        {
          client.s_server( msg )
        }
      }
    }
  }
}
