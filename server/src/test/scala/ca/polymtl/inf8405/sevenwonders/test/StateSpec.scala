package ca.polymtl.inf8405.sevenwonders
package test

import client._
import api.{GameRoomDef, GeoLocation}

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

      a.sender.s_connect("a")
      b.sender.s_connect("b")
      c.sender.s_connect("c")

      a.sender.s_create( new GameRoomDef( "1", new GeoLocation(1,1) ) )

      pa.expectMsgPF() { case CreatedGame => () }

      pb.expectMsgPF() { case CreatedGame => b.sender.s_listGamesRequest( new GeoLocation(1,1) ) }
      pb.expectMsgPF() { case GameListResponse( rooms ) => {
        val id = rooms.get(0).getId
        b.sender.s_join( id )
      }}

      pc.expectMsgPF() { case CreatedGame => c.sender.s_listGamesRequest( new GeoLocation(1,1) ) }
      pc.expectMsgPF() { case GameListResponse( rooms ) => {
        val id = rooms.get(0).getId
        c.sender.s_join( id )
      }}

      a.sender.s_start()

      a.stop()
    }
  }
}
