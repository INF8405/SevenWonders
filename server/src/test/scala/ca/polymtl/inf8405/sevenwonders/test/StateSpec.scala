package ca.polymtl.inf8405.sevenwonders
package test

import client._

import api.{Card => TCard, _}
import api.Card._
import api.Civilisation._
import api.CardCategory._

import app.ModelApiBridge
import model.collection.MultiMap
import model.Trade
import model.{Right,Left}
import model.Resource._

import java.util.{Map => JMap, Set => JSet, List => JList}

import akka.testkit.TestProbe


// Server/test-only ca.polymtl.inf8405.sevenwonders.test.StateSpec
class StateSpec extends ServerSpec {

  import scala.collection.JavaConversions._
  import scala.concurrent.duration._

  "a server" must {
    "create a state" in {

      val pBabylon = TestProbe()
      val babylon = new Client( system, pBabylon.ref, "babylon" )

      val pEphesos = TestProbe()
      val ephesos = new Client( system, pEphesos.ref, "ephesos" )

      val pHali = TestProbe()
      val hali = new Client( system, pHali.ref, "hali" )

      babylon.sender.s_connect("babylon")
      pBabylon.expectMsg( ConnectionResponse(true) )

      ephesos.sender.s_connect("ephesos")
      pEphesos.expectMsg( ConnectionResponse(true) )

      hali.sender.s_connect("hali")
      pHali.expectMsg( ConnectionResponse(true) )

      babylon.sender.s_create( new GameRoomDef( "1", new GeoLocation(1,1) ) )

      pBabylon.expectMsgPF() { case GameCreated => () }

      pEphesos.expectMsgPF() { case GameCreated => ephesos.sender.s_listGamesRequest( new GeoLocation(1,1) ) }
      pEphesos.expectMsgPF() { case GameListResponse( rooms ) => {
        val id = rooms.get(0).getId
        ephesos.sender.s_join( id )
      }}

      pHali.expectMsgPF() { case GameCreated => hali.sender.s_listGamesRequest( new GeoLocation(1,1) ) }
      pHali.expectMsgPF() { case GameListResponse( rooms ) => {
        val id = rooms.get(0).getId
        hali.sender.s_join( id )
      }}

      pBabylon.expectMsgAllOf( 3 seconds, Joined("ephesos"), Joined("hali") )

      babylon.sender.s_startStub()

      pBabylon.expectMsgPF() { case GameBegin( state ) => {

        state.players.size === 3

        val me = state.players.get(0)
        assert( me.civilisation == BABYLON_A )
        me.canPlayWonder === false

        val left = state.players.get(1)
        assert(isPlayerInInitialState(left))
        assert(left.civilisation == HALIKARNASSOS_B)

        val right = state.players.get(2)
        assert(isPlayerInInitialState(right))
        assert(right.civilisation == EPHESOS_B)

        assert( state.hand.unplayables.contains( BATHS ) )

        val playables = state.hand.playables
        playables.ac( CLAY_PIT )
        playables.ac( STONE_PIT )
        playables.ac( LUMBER_YARD )
        playables.ac( ALTAR )
        playables.ac( THEATER )
        playables.ac( WEST_TRADING_POST )

        babylon.sender.s_playCard( CLAY_PIT, NO_TRADE )
      }}

      pEphesos.expectMsgAllPF[Unit](List(
        { case Connected( players ) => {
          assert( players.containsAll( List("ephesos","babylon") : JList[String] ) )
        }},
        { case Joined("hali") => () },
        { case GameBegin( state ) => {

          state.players.size === 3

          val me = state.players.get(0)
          me.canPlayWonder === false
          me.civilisation === EPHESOS_B

          val left = state.players.get(1)
          assert(isPlayerInInitialState(left))
          assert(left.civilisation == BABYLON_A)

          val right = state.players.get(2)
          assert(isPlayerInInitialState(right))
          assert( right.civilisation == HALIKARNASSOS_B )

          assert( state.hand.unplayables.containsAll( List( WORKSHOP, STOCKADE ) ) )

          val playables = state.hand.playables

          playables.ac( CLAY_POOL )
          playables.ac( APOTHECARY, Set( MultiMap( Tapestry -> Right ) ) )
          playables.ac( MARKETPLACE )
          playables.ac( GLASSWORKS )
          playables.ac( LOOM )

          ephesos.sender.s_playCard( GLASSWORKS, NO_TRADE )
        }}
      ))


      pHali.expectMsgAllPF[Unit](List(
      { case Connected( players ) => {
        assert( players.containsAll( List("ephesos","babylon","hali") : JList[String] ) )
      }},
      { case GameBegin( state ) => {
        state.players.size === 3

        val me = state.players.get(0)
        me.canPlayWonder === false
        me.civilisation === HALIKARNASSOS_B

        val left = state.players.get(1)
        assert(isPlayerInInitialState(left))
        assert(left.civilisation == EPHESOS_B)

        val right = state.players.get(2)
        assert(isPlayerInInitialState(right))
        assert(right.civilisation == BABYLON_A)

        assert( state.hand.unplayables.containsAll( List( BARRACKS ) ) )

        val playables = state.hand.playables
        playables.ac( ORE_VEIN )
        playables.ac( GUARD_TOWER, Set( MultiMap( Clay -> Right ) ) )
        playables.ac( SCRIPTORIUM, Set( MultiMap( Paper -> Left ) ) )
        playables.ac( EAST_TRADING_POST )
        playables.ac( PRESS )
        playables.ac( TIMBER_YARD )

        hali.sender.s_playCard( TIMBER_YARD, NO_TRADE )
      }}))

      pBabylon.expectMsgPF() { case GameUpdate( state ) => {
        val me = state.players.get(0)
        me.tableau.get(RAW_MATERIAL) === ( List(CLAY_PIT): JList[TCard] )
        me.coins === 2
        me.canPlayWonder === true
        me.wonderTrades.isEmpty === true

        assert( state.hand.unplayables.isEmpty )

        val playables = state.hand.playables
        playables.ac(CLAY_POOL)
        playables.ac(MARKETPLACE)
        playables.ac(LOOM)
        playables.ac(WORKSHOP, Set( MultiMap( Glass -> Right ) ) )
        playables.ac(STOCKADE, Set( MultiMap( Wood -> Left ) ) )
        playables.ac(APOTHECARY, Set( MultiMap( Tapestry -> Left ) ) )

        babylon.sender.s_playCard( WORKSHOP, tt( MultiMap( Glass -> Right ) ) )
      }}

      pEphesos.expectMsgPF() { case GameUpdate( state ) => {
        val me = state.players.get(0)
        me.tableau.get(MANUFACTURED_GOOD) === ( List(GLASSWORKS): JList[TCard] )
        me.coins === 3
        me.canPlayWonder === false

        assert( state.hand.unplayables.isEmpty )

        val playables = state.hand.playables
        playables.ac(PRESS)
        playables.ac(EAST_TRADING_POST)
        playables.ac(ORE_VEIN)
        playables.ac(SCRIPTORIUM)
        playables.ac(BARRACKS, Set( MultiMap( Ore -> Left ) ) )
        playables.ac(GUARD_TOWER, Set( MultiMap( Clay -> Left ) ) )

        ephesos.sender.s_playCard( EAST_TRADING_POST, NO_TRADE )
      }}

      pHali.expectMsgPF() { case GameUpdate( state ) => {
        val me = state.players.get(0)
        me.tableau.get(RAW_MATERIAL) === ( List(TIMBER_YARD): JList[TCard] )
        me.coins === 2
        me.canPlayWonder === false

        val playables = state.hand.playables
        playables.ac( STONE_PIT )
        playables.ac( BATHS )
        playables.ac( WEST_TRADING_POST )
        playables.ac( THEATER )
        playables.ac( ALTAR )
        playables.ac( LUMBER_YARD )

        assert( state.hand.unplayables.isEmpty )

        hali.sender.s_playCard( BATHS, NO_TRADE )
      }}

      pBabylon.expectMsgPF() { case GameUpdate( state ) => {
        val me = state.players.get(0)
        me.tableau.get(RAW_MATERIAL) === ( List(CLAY_PIT): JList[TCard] )
        me.tableau.get(SCIENCE) === ( List(WORKSHOP): JList[TCard] )
        me.coins === 0
        me.canPlayWonder === true
        me.wonderTrades.isEmpty === true

        val playables = state.hand.playables
        playables.ac(ORE_VEIN)
        playables.ac(PRESS)
        playables.ac(BARRACKS)
        playables.ac(GUARD_TOWER)

        assert( state.hand.unplayables.contains( SCRIPTORIUM ) )

        babylon.sender.s_playCard( BARRACKS, NO_TRADE )
      }}

      pEphesos.expectMsgPF() { case GameUpdate( state ) => {
        val me = state.players.get(0)

        me.tableau.get(MANUFACTURED_GOOD) === ( List(GLASSWORKS): JList[TCard] )
        me.tableau.get(COMMERCIAL) === ( List(EAST_TRADING_POST): JList[TCard] )
        me.coins === 5
        me.canPlayWonder === false

        val playables = state.hand.playables
        playables.ac(LUMBER_YARD)
        playables.ac(ALTAR)
        playables.ac(THEATER)
        playables.ac(WEST_TRADING_POST)
        playables.ac(STONE_PIT)

        assert( state.hand.unplayables.isEmpty )
        ephesos.sender.s_playCard( WEST_TRADING_POST, NO_TRADE )
      }}

      pHali.expectMsgPF() { case GameUpdate( state ) => {
        val me = state.players.get(0)
        me.tableau.get(RAW_MATERIAL) === ( List(TIMBER_YARD): JList[TCard] )
        me.tableau.get(CIVILIAN) === ( List(BATHS): JList[TCard] )
        me.coins === 2
        me.canPlayWonder === false

        val playables = state.hand.playables
        playables.ac(CLAY_POOL)
        playables.ac(MARKETPLACE)
        playables.ac(LOOM)
        playables.ac(STOCKADE)
        playables.ac(APOTHECARY)

        assert( state.hand.unplayables.isEmpty )
        hali.sender.s_playCard( STOCKADE, NO_TRADE )
      }}

      pBabylon.expectMsgPF() { case GameUpdate( state ) => {
        val me = state.players.get(0)
        me.tableau.get(RAW_MATERIAL) === ( List(CLAY_PIT): JList[TCard] )
        me.tableau.get(MILITARY) === ( List(BARRACKS): JList[TCard] )
        me.tableau.get(SCIENCE) === ( List(WORKSHOP): JList[TCard] )
        me.coins === 0
        me.canPlayWonder === true
        me.wonderTrades.isEmpty === true


        val playables = state.hand.playables
        playables.ac(LUMBER_YARD)
        playables.ac(ALTAR)
        playables.ac(THEATER)
        playables.ac(STONE_PIT)

        println(state.hand.unplayables)

        assert( state.hand.unplayables.isEmpty )

        babylon.sender.s_playCard( LUMBER_YARD, NO_TRADE )
      }}

      pEphesos.expectMsgPF() { case GameUpdate( state ) => {
        val me = state.players.get(0)
        assert(me.tableau.get(COMMERCIAL).containsAll( List(WEST_TRADING_POST, EAST_TRADING_POST): JList[TCard] ))
        me.tableau.get(MANUFACTURED_GOOD) === ( List(GLASSWORKS): JList[TCard])
        me.coins === 5
        me.canPlayWonder === false


        val playables = state.hand.playables
        playables.ac(CLAY_POOL)
        playables.ac(MARKETPLACE)
        playables.ac(LOOM)
        playables.ac(APOTHECARY, Set( MultiMap( Tapestry -> Right)))

        assert( state.hand.unplayables.isEmpty )

        ephesos.sender.s_playCard( LOOM, NO_TRADE )
      }}

      pHali.expectMsgPF() { case GameUpdate( state ) => {
        val me = state.players.get(0)
        me.tableau.get(CIVILIAN) === ( List(BATHS): JList[TCard] )
        me.tableau.get(MILITARY) === ( List(STOCKADE): JList[TCard] )
        me.tableau.get(RAW_MATERIAL) === ( List(TIMBER_YARD): JList[TCard] )
        me.coins === 2
        me.canPlayWonder === false


        val playables = state.hand.playables
        playables.ac(PRESS)
        playables.ac(ORE_VEIN)
        playables.ac(GUARD_TOWER, Set( MultiMap( Clay -> Right)))
        playables.ac(SCRIPTORIUM, Set( MultiMap( Paper -> Left)))

        assert( state.hand.unplayables.isEmpty )

        hali.sender.s_playCard( ORE_VEIN, NO_TRADE )
      }}

//      pHali.expectMsgPF() { case GameUpdate( state ) => {
//        val me = state.players.get(0)
//        me.tableau.get(MANUFACTURED_GOOD) === ( List(GLASSWORKS): JList[TCard] )
//        me.coins ===
//          me.canPlayWonder ===
//
//
//        val playables = state.hand.playables
//        playables.ac()
//        playables.ac()
//        playables.ac()
//        playables.ac()
//        playables.ac()
//        playables.ac(, Set( MultiMap( ) ) )
//
//        assert( state.hand.unplayables.isEmpty )
//        hali.sender.s_playCard( , NO_TRADE )
//      }}

      // STOP
    }
  }

  private val NO_TRADE = new java.util.HashMap[api.Resource,JList[api.NeighborReference]]()
  private val NO_TRADES = new java.util.HashSet[JMap[api.Resource,JList[api.NeighborReference]]]()
  NO_TRADES.add(NO_TRADE)

  private def isPlayerInInitialState( player: Player ) = {
    player.tableau.size() === 0
    player.battleMarkers === 0
    player.coins === 3
    player.score === 0
    player.wonderStaged === 0
    player.canPlayWonder === false
  }

  def tt( trade: MultiMap[model.Resource,model.NeighborReference] ): JMap[api.Resource,JList[api.NeighborReference]] =
    ModelApiBridge.toThriftTrade(trade)

  implicit class ReceiveAllOf[T]( probe: TestProbe ) {
    def expectMsgAllPF[T]( handlers: List[PartialFunction[Any, T]] ): List[T] = {
      val messages = probe.receiveN( handlers.size )

      // Accumulate result, Every handler must map to one Message
      messages.foldRight( ( handlers, List.empty[T] ) ){ case ( message, ( accHandlers, res ) ) => {
        val handler = accHandlers.find( _.isDefinedAt( message ) )

        val newAcc = accHandlers.filterNot( _ == handler.get )
        val result = handler.map( _.apply( message ) ).getOrElse( fail(s"got unexpected message $message") )

        ( newAcc, result :: res )
      }}._2
    }
  }

  implicit class AssertPlayable( playables: JMap[api.Card,JSet[JMap[api.Resource,JList[api.NeighborReference]]]] ) {
    def ac( card: TCard, expectedTrades: Set[Trade] = Set() ) {

      import ModelApiBridge._

      assert( playables.containsKey( card ) )

      val actualTrades : Set[Trade] = playables.get(card).map( fromThriftTrade _ ).toSet
      assert( actualTrades == expectedTrades )
    }
  }
}