package ca.polymtl.inf8405.sevenwonders.app

import akka.actor.{TypedProps, TypedActor, ActorSystem}
import scala.concurrent._
import ca.polymtl.inf8405.sevenwonders.api.{GameRoom, GameRoomDef}

import ApiHelper._

import collection.mutable.{ Map => MMap }

trait GameLobby {
  def create( game: GameRoomDef, player: GameClient ): Future[TGame]
  def join( game: GameId, player: GameClient ): Future[TGame]
  def list: Future[List[GameRoom]]
}

class GameLobbyImpl( system: ActorSystem ) extends GameLobby {

  import system.dispatcher

  def create( definition: GameRoomDef, player: GameClient ) = {

    import java.util.UUID
    val id = UUID.randomUUID.toString

    val game: TGame = TypedActor( system ).typedActorOf( TypedProps[GameImpl]() )

    games( id ) = ( new GameRoom( id, definition ), game )

    future { game }
  }

  def join( id: GameId, player: GameClient ): Future[TGame] = {

    games.headOption.map{ case ( _, ( _, game ) ) => {
      game.join( player )
      future{ game }
    }}getOrElse( (Promise failed sys.error( "no games exists" )).future )

//    games.get( id ).map{ case ( _, game  ) => {
//      game.join( player )
//
//      future{ game }
//    }}.getOrElse( (Promise failed sys.error( s"game not found id=$id" )).future )
  }

  def list = future {
    games.values.map( _._1 ).toList
  }


  private val games = MMap.empty[GameId, (GameRoom, TGame)]
}