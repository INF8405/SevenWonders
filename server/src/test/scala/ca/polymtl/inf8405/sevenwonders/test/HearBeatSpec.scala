package ca.polymtl.inf8405.sevenwonders
package test

import ca.polymtl.inf8405.sevenwonders.test.client.{GameListResponse, Joined, Ping, Client}
import api.{GeoLocation, GameRoomDef}

import akka.testkit.TestProbe

class HearBeatSpec extends ServerSpec{

  "ping pong" must {
    "..." in {

      import scala.concurrent.duration._

      val probe = TestProbe()

      val client = new Client( system, probe.ref, "", ignorePing = false )

      client.sender.s_create( new GameRoomDef( "name", new GeoLocation(1,1) ) )

      probe.expectMsgPF(200 millis){
        case GameListResponse( _ ) => ()
        case Joined ( _ ) => ()
        case Ping => ()
      }

      client.stop()
    }
  }
}
