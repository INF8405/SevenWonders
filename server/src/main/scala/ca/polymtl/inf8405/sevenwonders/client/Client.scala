package ca.polymtl.inf8405.sevenwonders.client

import org.apache.thrift._
import transport.TSocket
import protocol.TBinaryProtocol

object Client extends App
{
  val transport = new TSocket("localhost", 8001)
  val protocol = new TBinaryProtocol( transport )

  val receiver = new MessageReceiver( protocol )
  val sender = new MessageSender( protocol )

  val monitor = new ConnectionStatusMonitor( transport )
  monitor.addListeners( receiver )
  monitor.addListeners( sender )

  monitor.tryOpen()

  ( 1 to 5 ).foreach( _ => {

    sender ! NewMessage( "hey" )

    Thread.sleep( 1000 )
  })
}
