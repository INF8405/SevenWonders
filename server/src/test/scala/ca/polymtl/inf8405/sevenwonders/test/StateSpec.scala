package ca.polymtl.inf8405.sevenwonders
package test

import client.{Joined, GameListResponse, Client}
import api.{GameRoomDef, GeoLocation}

import java.util.{ List => JList }
import akka.testkit.TestProbe

class StateSpec extends ServerSpec {

  "a server" must {
    "create a state" in {

      val pa = TestProbe()
      val a = new Client( system, pa.ref, "a" )

      val pb = TestProbe()
      val b = new Client( system, pb.ref, "b" )

      val pc = TestProbe()
      val c = new Client( system, pc.ref, "c" )

//    val clients = List(a,b,c)

      a.sender.s_create( new GameRoomDef( "1", new GeoLocation(1,1) ) )
      Thread.sleep(1000)
      b.sender.s_listGamesRequest( new GeoLocation(1,1) )

      pa.expectMsgPF(){ case GameListResponse(_) => () }
      pb.expectMsgPF() {
        case GameListResponse( rooms ) => {

          rooms.size() == 1

          // all join
          val id = rooms.get(0).getId
          b.sender.s_join( id )
          c.sender.s_join( id )
        }
      }

      pa.expectMsgPF(){ case Joined( _ ) => () }



      a.stop()
    }
  }
}
