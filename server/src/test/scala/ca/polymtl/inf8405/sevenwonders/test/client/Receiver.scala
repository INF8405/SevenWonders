package ca.polymtl.inf8405.sevenwonders
package test.client

import api._

import org.apache.thrift.protocol.TProtocol
import akka.actor.{ActorRef, ActorSystem}
import concurrent.Future

trait Mock extends SevenWondersApi.Iface
trait Receiver extends SevenWondersApi.Iface {
  def start()
  def stop()
}

case class ConnectionResponse( connected: Boolean )
case object Ping
case class GameListResponse( rooms: JList[GameRoom] )
case class Joined( user: String )
case class Connected( users: JList[String] )
case class Left( user: String )
case object GameCreated
case class GameBegin( state: GameState )
case class GameUpdate( state: GameState )
case class GameEnd( state: GameState, detail: JList[JMap[String, Integer]] )

class ReceiverImpl( sender: SevenWondersApi.Client, protocol: TProtocol, system: ActorSystem, probe: ActorRef, name: String, ignorePing : Boolean ) extends Receiver {

  def start(){
    import system.dispatcher
    Future {
      while( !_stop ) {
        processor.process( protocol, protocol )
      }
    }
  }

  def stop(){ _stop = true }


  def c_connectionResponse(connected: Boolean) {
    probe ! ConnectionResponse( connected )
  }

  def c_listGamesResponse(rooms: JList[GameRoom]) {
    probe ! GameListResponse( rooms )
  }

  def c_joined(user: String) {
    probe ! Joined( user )
  }

  def c_connected(users: JList[String]) {
    probe ! Connected( users )
  }

  def c_left(user: String) {
    probe ! Left( user )
  }

  def c_createdGame() {
    probe ! GameCreated
  }

  def c_begin(state: GameState) {
    probe ! GameBegin( state )
  }

  def c_sendState(state: GameState) {
    probe ! GameUpdate( state )
  }

  def c_sendEndState(state: GameState, detail: JList[JMap[String, Integer]]) {
    probe ! GameEnd( state, detail )
  }

  def c_ping() {
    sender.s_pong()
    if( !ignorePing ) {
      probe ! Ping
    }
  }

  def s_connect(username: String) { SERVER }
  def s_reconnect(username: String) { SERVER }
  def s_playCard(card: api.Card, trade: JMap[Resource, JList[NeighborReference]]) { SERVER }
  def s_playWonder(card: api.Card, trade: JMap[Resource, JList[NeighborReference]]) { SERVER }
  def s_discard(card: api.Card) { SERVER }
  def s_create(definition: GameRoomDef) { SERVER }
  def s_listGamesRequest(geo: GeoLocation) { SERVER }
  def s_start() { SERVER }
  def s_startStub() { SERVER }
  def s_pong() { SERVER }
  def s_join(id: String) { SERVER }

  private val processor = new SevenWondersApi.Processor( this )
  private var _stop = false
}
