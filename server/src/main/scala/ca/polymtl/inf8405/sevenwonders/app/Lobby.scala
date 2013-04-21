package ca.polymtl.inf8405.sevenwonders.app

import akka.actor.{TypedProps, TypedActor, ActorSystem}
import scala.concurrent._
import ca.polymtl.inf8405.sevenwonders.api.{GameRoom, GameRoomDef}

import ApiHelper._

import collection.mutable.{ Map => MMap, Set => MSet }

trait Lobby {
  def connect( user: User )
  def create( game: GameRoomDef, user: User, dispatch: Dispatcher ): Future[TGame]
  def join( game: GameId, user: User ): Future[TGame]
  def list: Future[List[GameRoom]]
}

class LobbyImpl( system: ActorSystem ) extends Lobby {

  import system.dispatcher

  def connect( user: User ) {
    if( users.contains( user ) ) {
      user.api.c_connectionResponse( false )
    } else {
      users += user
      user.api.c_connectionResponse( true )
    }
  }

  def create( definition: GameRoomDef, user: User, dispatch: Dispatcher ) = {
    import java.util.UUID
    val id = UUID.randomUUID.toString
    val game: TGame = TypedActor( system ).typedActorOf( TypedProps( classOf[TGame], new GameImpl( system ) ) )

    games( id ) = ( new GameRoom( id, definition ), game )
    usersInGames( user ) = game
    game.created( user )

    users foreach( _.api.c_createdGame() )

    future { game }
  }

  def join( id: GameId, user: User ): Future[TGame] = {
    games.get( id ).map{ case ( _, game  ) => {
      usersInGames( user ) = game
      game.join( user )
      future{ game }
    }}.getOrElse( (Promise failed sys.error( s"game not found id=$id" )).future )
  }

  def list = {
    future {
      games.values.map( _._1 ).toList
    }
  }

  private val users = MSet.empty[User]
  private val usersInGames = MMap.empty[User,TGame]
  private val games = MMap.empty[GameId, (GameRoom, TGame)]
}