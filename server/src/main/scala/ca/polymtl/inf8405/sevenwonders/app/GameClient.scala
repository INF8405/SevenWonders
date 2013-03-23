package ca.polymtl.inf8405.sevenwonders
package app

import api._
import api.SevenWondersApi.Client

import org.apache.thrift.transport.TTransport
import org.apache.thrift.protocol.TBinaryProtocol
import java.net.InetAddress
import akka.actor.Actor

class GameClient( transport: TTransport, inet: InetAddress ) extends Actor with SevenWondersApi.Iface
{
  val client = new Client( new TBinaryProtocol( transport ) )

  def receive = {
    case Ping => client.c_ping()
  }

  def s_listGames(geo: GeoLocation): java.util.List[GameRoom] = ???
  def s_create(definition: GameRoomDef) {}
  def s_join(id: Int) {}
  def s_start() {}

  def s_playCard(card: String, trade: java.util.Map[Resource, java.util.List[NeighborReference]]) {}
  def s_playWonder(trade: java.util.Map[Resource, java.util.List[NeighborReference]]) {}
  def s_discard(card: String) {}

  def s_pong() {}

  def c_joined(user: String) {}
  def c_sendState(state: GameState) {}
  def c_sendEndState(state: GameState, detail: java.util.List[java.util.Map[String, Integer]]) {}
  def c_ping() {}
}