package ca.polymtl.inf8405.sevenwonders
package test

import ca.polymtl.inf8405.sevenwonders.test.client.{GameListResponse, Client}
import ca.polymtl.inf8405.sevenwonders.api.{GameRoomDef, GeoLocation}
import akka.testkit.TestProbe

class GeoServerSpec extends ServerSpec {

  "a server" must {
    "can create a game based on the client geo location" in {

      val probe = TestProbe()

      val client = new Client( system, probe.ref, "" )
      client.sender.s_create( new GameRoomDef( "1", new GeoLocation(1,1) ) )
      client.sender.s_listGamesRequest( new GeoLocation(1,1) )

      probe.expectMsgPF() {
        case GameListResponse( rooms ) => ()
        case _ => fail()
      }

      client.stop()
    }
  }
}
