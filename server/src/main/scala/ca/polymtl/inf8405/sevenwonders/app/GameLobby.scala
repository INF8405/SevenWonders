package ca.polymtl.inf8405.sevenwonders.app

import akka.actor.{TypedProps, TypedActor, ActorSystem}
import ca.polymtl.inf8405.sevenwonders.api.{GameRoom, GameRoomDef}

import ApiHelper._

import collection.mutable.{ Map => MMap }

trait GameLobby {
  def create( game: GameRoomDef, player: GameClient ): Game
  def join( game: GameId, player: GameClient ): Option[Game]
  def list: List[GameRoom]
}

class GameLobbyImpl( system: ActorSystem ) extends GameLobby {

  def create( definition: GameRoomDef, player: GameClient ): Game = {

    import java.util.UUID
    val id = UUID.randomUUID.toString

    val game: Game = TypedActor( system ).typedActorOf( TypedProps[GameImpl]() )

    games( id ) = ( new GameRoom( id, definition ), game )

    game
  }

  def join( id: GameId, player: GameClient ): Option[Game] = {
    val game = games.get( id )

    for { ( _, g ) <- game } {
      g.join( player )
    }

    game.map( _._2 )
  }

  def list: List[GameRoom] = {
    games.values.map( _._1 ).toList
  }


  private val games = MMap.empty[GameId, (GameRoom, Game)]
}