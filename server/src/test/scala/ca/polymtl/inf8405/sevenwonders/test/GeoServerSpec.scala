package ca.polymtl.inf8405.sevenwonders
package test

import client.{DispatcherMock, Client}
import ca.polymtl.inf8405.sevenwonders.api.{GameRoom, GameRoomDef, GeoLocation}
import java.util.{ List => JList }
import org.scalatest.time.SpanSugar._

class GeoServerSpec extends ServerSpec {

  "a server" must {
    "can create a game based on the client geolocation" in {

      var games = Option.empty[JList[GameRoom]]

      val client = new Client( system, new DispatcherMock{
        override def c_listGamesResponse(rooms: JList[GameRoom]) {
          games = Some( rooms )
        }
      })
      client.sender.s_create( new GameRoomDef( "1", new GeoLocation("1","1") ) )
      client.sender.s_listGamesRequest( new GeoLocation("1","1") )

      eventually( timeout( 1 second ) ){
        games.isEmpty === false
        games.get.size() === 1
      }

      client.stop()
    }
  }
}
