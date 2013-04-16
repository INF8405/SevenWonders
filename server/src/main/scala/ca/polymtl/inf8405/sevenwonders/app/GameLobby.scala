package ca.polymtl.inf8405.sevenwonders.app

import akka.actor.{TypedProps, TypedActor, ActorSystem}
import scala.concurrent._
import ca.polymtl.inf8405.sevenwonders.api.{GameRoom, GameRoomDef}

import ApiHelper._

import collection.mutable.{ Map => MMap }

trait GameLobby {
  def create( game: GameRoomDef, player: GameClient, dispatch: Dispatcher ): Future[TGame]
  def join( game: GameId, player: GameClient ): Future[TGame]
  def list: Future[List[GameRoom]]
}

class GameLobbyImpl( system: ActorSystem ) extends GameLobby {

  import system.dispatcher

  def create( definition: GameRoomDef, player: GameClient, dispatch: Dispatcher ) = {
    import java.util.UUID
    val id = UUID.randomUUID.toString
    val game: TGame = TypedActor( system ).typedActorOf( TypedProps( classOf[TGame], new GameImpl( system ) ) )
    games( id ) = ( new GameRoom( id, definition ), game )
    game.join( player )

    dispatch.created()

    future { game }
  }

  def join( id: GameId, player: GameClient ): Future[TGame] = {
    games.get( id ).map{ case ( _, game  ) => {
      game.join( player )
      future{ game }
    }}.getOrElse( (Promise failed sys.error( s"game not found id=$id" )).future )
  }

  def list = {
    future {
      games.values.map( _._1 ).toList
    }
  }

  private val games = MMap.empty[GameId, (GameRoom, TGame)]
}