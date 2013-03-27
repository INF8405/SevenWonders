package ca.polymtl.inf8405.sevenwonders.app

import org.apache.thrift.TProcessorFactory
import org.apache.thrift.transport.TTransport

class MessageProcessorFactory( dispatcher: Dispatcher ) extends TProcessorFactory( null ) {

  override def getProcessor( transport: TTransport ) = {
    dispatcher.getOrAddProcessor( transport )
  }
}
