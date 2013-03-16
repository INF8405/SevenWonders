package ca.polymtl.inf8405.sevenwonders.client

import org.apache.thrift.transport.TTransport
import org.apache.thrift.TException
import actors.Actor

class ConnectionStatusMonitor( transport: TTransport )
{
  var listeners = List.empty[Actor]

  def addListeners( actor: Actor )
  {
    listeners = actor :: listeners
  }

  def tryOpen()
  {
    try
    {
      transport.open()
      println("connected")
      listeners.foreach( a => a.start() )
    }
    catch
      {
        case e: TException =>
        {
          Thread.sleep(1000)
          println("cannot connect")
          tryOpen()
        }
      }
  }
}

