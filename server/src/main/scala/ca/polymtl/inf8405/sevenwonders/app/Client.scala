package ca.polymtl.inf8405.sevenwonders
package app

import api._
import api.{ Card => TCard }
import api.SevenWondersApi.{ Client => TClient }

import org.apache.thrift._
import org.apache.thrift.transport.TTransport
import protocol.TBinaryProtocol

import java.util.{ List => JList, Map => JMap }
import api.Resource
import akka.actor.{TypedActor, ActorSystem}


object ApiHelper {
  type GameId = String
  type TTrade = JMap[Resource, JList[NeighborReference]]
}

case class User( api: Client, username : String ) {
  override def equals(obj: Any) = {
    obj match {
      case a: User => username == a.username
      case _ => false
    }
  }
  override def hashCode = username.hashCode()
}

trait Client extends SevenWondersApi.Iface {
  def disconnect()
}

class ClientImpl( transport: TTransport, lobby: Lobby, dispatch: Dispatcher, system: ActorSystem ) extends Client
{
  import ApiHelper._
  import collection.JavaConversions._
  import system.dispatcher


  def s_connect(username: String) {
    val u = new User( TypedActor.self, username )
    user = Some( u )
    lobby.connect( u )
  }

  def s_reconnect(username: String) {
    // TODO: Implement me
    ???
  }

  def s_create( definition: GameRoomDef ) {
    for {
      u <- user
      g <- lobby.create( definition, u, dispatch ) } {

      game = Some( g )
    }
  }

  def s_listGamesRequest(geo: GeoLocation) {
    for { _ <- user } {
      lobby.list.foreach( l => {
        client.c_listGamesResponse( l )
      })
    }
  }

  def s_join( id: GameId ) {
    for {
      u <- user
      g <- lobby.join( id, u ) } {

      game = Some( g )
    }
  }

  def s_start() {
    for { g <- game } {
      g.start()
    }
  }

  def s_startStub() {
    for { g <- game } {
      g.startStub()
    }
  }

  def s_playCard( card: TCard, trade: TTrade ) {
    for {
      u <- user
      g <- game } {

      g.playCard( u, card, trade )
    }
  }

  def s_playWonder( trade: TTrade ) {
    for {
      u <- user
      g <- game } {

      g.playWonder( u, trade )
    }
  }
  def s_discard( card: TCard ) {
    for {
      u <- user
      g <- game } {

      g.discard( u, card )
    }
  }

  def disconnect() {
    // todo: do something
  }

  def s_pong() {
    dispatch.pong( transport )
  }

  def c_connectionResponse(connected: Boolean) {
    client.c_connectionResponse(connected)
  }

  def c_listGamesResponse(rooms: JList[GameRoom]) {
    client.c_listGamesResponse( rooms )
  }

  def c_joined( user: String) {
    client.c_joined( user )
  }

  def c_connected(users: JList[String]) {
    client.c_connected( users )
  }

  def c_createdGame() {
    client.c_createdGame()
  }

  def c_left(user: String) {
    client.c_left( user )
  }
  def c_begin(state: GameState) {
    client.c_begin( state )
  }
  def c_sendState(state: GameState) {
    client.c_sendState( state )
  }
  def c_sendEndState(state: GameState, detail: JList[JMap[String, Integer]]) {
    client.c_sendEndState( state, detail )
  }
  def c_ping() {
    client.c_ping()
  }

  private var game: Option[TGame] = None
  private val client = new TClient( new TBinaryProtocol( transport ) )
  private var user: Option[User] = None
}