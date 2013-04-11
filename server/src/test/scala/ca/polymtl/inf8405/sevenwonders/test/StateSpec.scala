package ca.polymtl.inf8405.sevenwonders
package test

import client.{DispatcherMock, Client}
import ca.polymtl.inf8405.sevenwonders.api.{GameRoom, GameRoomDef, GeoLocation}
import java.util.{ List => JList }
import org.scalatest.time.SpanSugar._

class StateSpec extends ServerSpec {

  "a server" must {
    "create a state" in {

      val a = new Client( system, new DispatcherMock )
      val b = new Client( system, new DispatcherMock )
      val c = new Client( system, new DispatcherMock )
      a.sender.s_create( new GameRoomDef( "1", new GeoLocation("1","1") ) )



      a.stop()
    }
  }
}
