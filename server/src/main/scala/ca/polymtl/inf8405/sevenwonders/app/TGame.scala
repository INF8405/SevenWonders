package ca.polymtl.inf8405.sevenwonders
package app

import ApiHelper._
import model.SevenWonders._

import ca.polymtl.inf8405.sevenwonders.api.{Player => TPlayer, GameState => TGameState, Hand, Resource => TResource, NeighborReference}

import collection.mutable.{ Set => MSet, Map => MMap }
import java.util.{ List => JList, Map => JMap, Set => JSet }

trait TGame {

  def join( player: GameClient )
  def disconnect( player: GameClient )
  def start()

  def playCard( card: TCard, trade: TTrade )
  def playWonder( trade: TTrade )
  def discard( card: TCard )
}

class GameImpl extends TGame {

  import collection.JavaConversions._

  def join( player: GameClient ){
    players.add( player )
  }

  def disconnect( player: GameClient ){
    players.remove( player )

  }

  def start(){
    val game = beginGame( players.size, playWithCities = false )
    playerBridge = game.players.zip( players ).map( _.swap ).toMap
    gameImpl = Some( game )

    playerBridge.foreach{ case ( client, player ) => {
      client.c_sendState( toThriftState( player, game ) )
    }}
  }

  def playCard(card: TCard, trade: TTrade ) {}
  def playWonder( trade: TTrade ) {}
  def discard(card: TCard) {}

  def broadcast( f: GameClient => Unit ) {
    players.foreach( f(_) )
  }

  def toThriftState( player: Player, game: Game ): TGameState = {

    def getNeighborProductions( player: Player ) = {
      Map(
        Left -> game.players.getLeft( player ).tradableProduction,
        Right -> game.players.getRight( player ).tradableProduction
      )
    }

    def toThriftPlayer(p: Player ): TPlayer = {
      val bridge: Map[Class[_ <: Card], String] = Map( classOf[ScienceCard] -> "Science" )

      val tableau: JMap[String,JList[String]] =
        p.played.groupBy( _.getClass ).map{ case ( ( clazz, cards ) ) => ( bridge(clazz), (cards.map( _.name ).toList): JList[String] ) }

      new TPlayer(
        tableau,
        player.civilization.name,
        player.battleMarkers.map( _.vicPoints: Integer ).toList: JList[Integer],
        player.coins,
        player.score( game.getNeighboorsStuff( player ) ),
        player.nbWonders,
        player.canBuildWonderStage( getNeighborProductions( player ) )
      )
    }

    // Playables
    val neighborProductions = getNeighborProductions( player )
    val playableCards = player.playableCards( neighborProductions )
    val playables: Map[TCard,Set[Trade]] = playableCards.map( card => ( card.name, player.possibleTrades( card, neighborProductions ) ) ).toMap

    val bridgeRessource: Map[Resource, TResource] = Map(
      Clay -> TResource.Clay,
      Wood -> TResource.Wood,
      Ore -> TResource.Ore,
      Stone -> TResource.Stone,
      Glass -> TResource.Glass,
      Paper -> TResource.Paper,
      Tapestry -> TResource.Tapestry
    )

    val bridgeNeighbor = Map(
      Left -> NeighborReference.Left,
      Right -> NeighborReference.Right
    )

    val tPlayables = playables.mapValues(
      _.map(
        _.toMap.map{
          case ( resource, neighbors ) =>
            ( bridgeRessource(resource), neighbors.map( bridgeNeighbor ).toList : JList[NeighborReference] )
        } : JMap[TResource, JList[NeighborReference]]
      ) : JSet[JMap[TResource, JList[NeighborReference]]]
    ): JMap[String,JSet[JMap[TResource, JList[NeighborReference]]]]

    // players
    val ( before, me :: after ) = game.players.toList.span( _ == player )
    val players = me :: before.reverse ::: after.reverse

    val unplayables = (player.hand - playableCards).map( _.name ).toList : JList[String]

    new TGameState(
      new Hand( tPlayables, unplayables ),
      players.map( toThriftPlayer _ ) : JList[TPlayer]
    )
  }


  private val players = MSet.empty[GameClient]
  private var playerBridge = Map.empty[GameClient,Player]
  private var gameImpl = Option.empty[Game]

  implicit def toJMap[A,B]( me: Map[A,B] ): JMap[A,B] = collection.mutable.Map( me.toSeq: _* )
}