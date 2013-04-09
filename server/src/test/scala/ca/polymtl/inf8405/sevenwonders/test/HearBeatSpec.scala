package ca.polymtl.inf8405.sevenwonders
package test

import client.{DispatcherMock, Client}
import api.{GeoLocation, GameRoomDef}

import org.scalatest.time.SpanSugar._

class HearBeatSpec( ) extends ServerSpec{

  "ping pong" must {
    "..." in {

      var pong = 0

      val client = new Client( system, new DispatcherMock(){
        override def c_ping() {
          pong += 1
        }
      })

      client.sender.s_create( new GameRoomDef( "name", new GeoLocation("1","1") ) )

      eventually( timeout( 1 second ) ){
        assert( pong >= 1 )
      }

      client.stop()
    }
  }
}
