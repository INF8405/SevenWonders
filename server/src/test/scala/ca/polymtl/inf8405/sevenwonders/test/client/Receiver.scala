package ca.polymtl.inf8405.sevenwonders
package test.client

import api._

import org.apache.thrift.protocol.TProtocol
import akka.actor.ActorSystem
import java.util
import concurrent.Future


class DispatcherMock extends Mock {

  def c_listGamesResponse(rooms: util.List[GameRoom]) {}
  def c_joined(user: String) {}
  def c_left(user: String) {}
  def c_ping() {}
  def c_sendState(state: GameState) {}
  def c_sendEndState(state: GameState, detail: util.List[util.Map[String, Integer]]) {}

  def s_start() { SERVER }
  def s_listGamesRequest(geo: GeoLocation) { SERVER }
  def s_pong() { SERVER }
  def s_join(id: String) { SERVER }
  def s_playCard(card: String, trade: JMap[Resource, JList[NeighborReference]]) { SERVER }
  def s_playWonder(trade: JMap[Resource, JList[NeighborReference]]) { SERVER }
  def s_discard(card: String) { SERVER }
  def s_create(definition: GameRoomDef) { SERVER }
  def s_join(id: Int) { SERVER }
}

trait Mock extends SevenWondersApi.Iface
trait Receiver extends SevenWondersApi.Iface {
  def start()
  def stop()
}

class ReceiverImpl( sender: SevenWondersApi.Client, protocol: TProtocol, system: ActorSystem, mock: Mock ) extends Receiver {

  def start(){
    import system.dispatcher
    Future {
      while( !_stop ) {
        processor.process( protocol, protocol )
      }
    }
  }

  def stop(){
    _stop = true
  }

  def c_listGamesResponse(rooms: util.List[GameRoom]) {
    mock.c_listGamesResponse( rooms )
  }

  def c_joined(user: User) {
    mock.c_joined( user )
  }
  def c_sendState(state: GameState) {
    mock.c_sendState( state )
  }
  def c_sendEndState(state: GameState, detail: JList[JMap[Category, Integer]]) {
    mock.c_sendEndState( state, detail )
  }
  def c_ping() {
    mock.c_ping()
    sender.s_pong()
  }
  def c_left(user: User) {
    mock.c_left(user)
  }

  def s_join(id: String) {}

  def s_start() { SERVER }
  def s_pong() { SERVER }
  def s_listGamesRequest(geo: GeoLocation) { SERVER }
  def s_playCard(card: String, trade: JMap[Resource, JList[NeighborReference]]) { SERVER }
  def s_playWonder(trade: JMap[Resource, JList[NeighborReference]]) { SERVER }
  def s_discard(card: String) { SERVER }
  def s_create(definition: GameRoomDef) { SERVER }
  def s_join(id: Int) { SERVER }

  private val processor = new SevenWondersApi.Processor( this )
  private var _stop = false
}
