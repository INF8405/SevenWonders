package ca.polymtl.inf8405.sevenwonders
package app

import api._
import api.SevenWondersApi.Client

import org.apache.thrift.transport.TTransport
import org.apache.thrift.protocol.TBinaryProtocol
import java.net.InetAddress

import java.util.{ List => JList, Map => JMap }
import api.Resource
import akka.actor.TypedActor

object ApiHelper {
  type GameId = String
  type Card = String
  type Player = InetAddress
  type Trade = JMap[Resource, JList[NeighborReference]]
}

trait GameClient extends SevenWondersApi.Iface {
  def disconnect()
}

class GameClientImpl( transport: TTransport, val ip: InetAddress, lobby: GameLobby ) extends GameClient
{
  import ApiHelper._
  import collection.JavaConversions._

  val client = new Client( new TBinaryProtocol( transport ) )

  def s_listGames(geo: GeoLocation): java.util.List[GameRoom] = lobby.list

  def s_create( definition: GameRoomDef ) {
    game = Some( lobby.create( definition, TypedActor.self ) )
  }

  def s_join( id: GameId ) {
    game = lobby.join( id, TypedActor.self )
  }

  def s_start() {
    game.foreach( _.start() )
  }

  def s_playCard( card: String, trade: Trade ) {
    game.foreach( _.playCard( card, trade ) )
  }

  def s_playWonder( trade: Trade ) {
    game.foreach( _.playWonder( trade ) )
  }
  def s_discard( card: Card ) {
    game.foreach( _.discard( card ) )
  }

  def disconnect() {
    game.foreach( _.disconnect( TypedActor.self ) )
  }

  def s_pong() {}

  def c_joined( user: String) {}
  def c_left(user: String) {}
  def c_sendState(state: GameState) {}
  def c_sendEndState(state: GameState, detail: java.util.List[java.util.Map[String, Integer]]) {}
  def c_ping() {}


  private var game: Option[Game] = None

  override def equals( other: Any ) = {
    other match {
      case a: GameClientImpl => a.ip == ip
      case _ => false
    }
  }

  override def hashCode = ip.hashCode
}