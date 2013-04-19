package ca.polymtl.inf8405.sevenwonders
package app

import ApiHelper._

import model._
import model.SevenWonders._
import model.CardCollection._
import model.CivilizationCollection._
import model.collection.Circle
import model.collection.MultiSet

import api.{
  Player => TPlayer,
  GameState => TGameState,
  Resource => TResource,
  Card => TCard,
  CardCategory,
  Hand,
  NeighborReference,
  Civilisation => TCivilisation
}

import akka.actor.ActorSystem

import scala.collection.mutable.{ Set => MSet, Map => MMap }
import java.util.{ List => JList, Map => JMap, Set => JSet }


trait TGame {

  def created( user: User )
  def join( user: User )
  def disconnect( user: User )

  def start()
  def startStub()

  def playCard( user: User, card: TCard, trade: TTrade )
  def playWonder( user: User, trade: TTrade )
  def discard( user: User, card: TCard )
}

class GameImpl( system: ActorSystem ) extends TGame {

  import scala.collection.JavaConversions._

  def created( user: User ) {
    users.add( user )
  }

  def join( user: User ){
    users.add( user )
    broadcastOthers( user ) { _.c_joined( user.username ) }
    user.api.c_connected( users.map( _.username ).toList )
  }

  def disconnect( user: User ){
    // todo: handle reconnect
  }

  def start(){
    val game = beginGame( users.size, playWithCities = false )
    usersPlayers = game.players.zip( users ).map( _.swap ).toMap
    gameImpl = Some( game )

    usersPlayers.foreach{ case ( client, player ) => {
      client.api.c_begin( toThriftState( player, game ) )
    }}
  }

  def startStub(){
    val hand1 = MultiSet[Card]( WEST_TRADING_POST, THEATER, ALTAR, LUMBER_YARD, BATHS, STONE_PIT, CLAY_PIT )
    val player1 = Player( civilization = BABYLON_A, hand = hand1, coins = 3 )

    val hand2 = MultiSet[Card]( CLAY_POOL, APOTHECARY, WORKSHOP, MARKETPLACE, STOCKADE, GLASSWORKS, LOOM )
    val player2 = Player( civilization = EPHESOS_B, hand = hand2, coins = 3 )

    val hand3 = MultiSet[Card]( TIMBER_YARD, PRESS, EAST_TRADING_POST, BARRACKS, SCRIPTORIUM, GUARD_TOWER, ORE_VEIN )
    val player3 = Player( civilization = HALIKARNASSOS_B, hand = hand3, coins = 3 )

    val game = Game(
      new Circle[Player]( player1, player2, player3 ),
      Map( 1 -> MultiSet(DUMMY_CARD), 2 -> MultiSet(DUMMY_CARD) )
    )

    val u1 = users.find( _.username == "babylon" ).get
    val u2 = users.find( _.username == "ephesos" ).get
    val u3 = users.find( _.username == "hali" ).get

    usersPlayers = Map (
      u1 -> player1,
      u2 -> player2,
      u3 -> player3
    )

    usersPlayers.foreach{ case ( client, player ) => {
      client.api.c_begin( toThriftState( player, game ) )
    }}
  }

  def playCard( user: User, card: TCard, trade: TTrade ) {
    // todo: do it
  }

  def playWonder( user: User, trade: TTrade ) {
    // todo: do it
  }

  def discard( user: User, card: TCard ) {
    // todo: do it
  }

  def broadcast( f: Client => Unit ) {
    users foreach( user => f( user.api ) )
  }

  def broadcastOthers( me: User )( f: Client => Unit ) {
    users filterNot( _ == me ) foreach( user => f( user.api ) )
  }

  def toThriftState( player: Player, game: Game ): TGameState = {

    def getNeighborProductions( player: Player ) = {
      Map(
        Left -> game.players.getLeft( player ).tradableProduction,
        Right -> game.players.getRight( player ).tradableProduction
      )
    }

    def toThriftPlayer(p: Player ): TPlayer = {
      val bridgeCategory: Map[Class[_ <: Card], CardCategory] = Map(
        classOf[ScienceCard] -> CardCategory.SCIENCE,
        classOf[MilitaryCard] -> CardCategory.MILITARY,
        classOf[RawMaterialCard] -> CardCategory.RAW_MATERIAL,
        classOf[ManufacturedGoodCard] -> CardCategory.MANUFACTURED_GOOD,
        classOf[CivilianCard] -> CardCategory.CIVILIAN,
        classOf[CommercialCard] -> CardCategory.COMMERCIAL,
        classOf[GuildCard] -> CardCategory.GUILD
      )

      val tableau: JMap[CardCategory,JList[TCard]] =
        p.played.groupBy( _.getClass ).map{ case ( ( clazz, cards ) ) => {
          ( bridgeCategory(clazz), (cards.map( card => TCard.valueOf( card.name ) ).toList): JList[TCard] )
        }}

      new TPlayer(
        tableau,
        TCivilisation.valueOf(player.civilization.name),
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
    val playables: Map[TCard,Set[Trade]] = playableCards.map( card => ( TCard.valueOf( card.name ), player.possibleTrades( card, neighborProductions ) ) ).toMap

    val bridgeRessource: Map[Resource, TResource] = Map(
      Clay -> TResource.CLAY,
      Wood -> TResource.WOOD,
      Ore -> TResource.ORE,
      Stone -> TResource.STONE,
      Glass -> TResource.GLASS,
      Paper -> TResource.PAPER,
      Tapestry -> TResource.TAPESTRY
    )

    val bridgeNeighbor = Map(
      Left -> NeighborReference.LEFT,
      Right -> NeighborReference.RIGHT
    )

    val tPlayables = playables.mapValues(
      _.map(
        _.toMap.map{
          case ( resource, neighbors ) =>
            ( bridgeRessource(resource), neighbors.map( bridgeNeighbor ).toList : JList[NeighborReference] )
        } : JMap[TResource, JList[NeighborReference]]
      ) : JSet[JMap[TResource, JList[NeighborReference]]]
    ): JMap[TCard,JSet[JMap[TResource, JList[NeighborReference]]]]

    // players
    val ( before, me :: after ) = game.players.toList.span( _ == player )
    val players = me :: before.reverse ::: after.reverse

    val unplayables = (player.hand -- playableCards).map( card => TCard.valueOf( card.name ) ).toList : JList[TCard]

    new TGameState(
      new Hand( tPlayables, unplayables ),
      players.map( toThriftPlayer _ ) : JList[TPlayer]
    )
  }


  private val users = MSet.empty[User]
  private var usersPlayers = Map.empty[User,Player]
  private var gameImpl = Option.empty[Game]

  implicit def toJMap[A,B]( me: Map[A,B] ): JMap[A,B] = MMap( me.toSeq: _* )
}