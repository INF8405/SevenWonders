package ca.polymtl.inf8405.sevenwonders
package test
import client._

import api._
import api.{Card => TCard}
import api.Card._
import api.Resource._
import api.NeighborReference._
import api.CardCategory._
import api.Civilisation._

import java.util.{Map => JMap, Set => JSet, List => JList}


import akka.testkit.TestProbe
import scala.collection.immutable.HashMap

// Server/test-only ca.polymtl.inf8405.sevenwonders.test.StateSpec
class StateSpec extends ServerSpec {

  import collection.JavaConversions._
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
      ephesos.sender.s_connect("ephesos")
      hali.sender.s_connect("hali")

      babylon.sender.s_create( new GameRoomDef( "1", new GeoLocation(1,1) ) )

      pBabylon.expectMsgPF() { case CreatedGame => () }

      pEphesos.expectMsgPF() { case CreatedGame => ephesos.sender.s_listGamesRequest( new GeoLocation(1,1) ) }
      pEphesos.expectMsgPF() { case GameListResponse( rooms ) => {
        val id = rooms.get(0).getId
        ephesos.sender.s_join( id )
      }}

      pHali.expectMsgPF() { case CreatedGame => hali.sender.s_listGamesRequest( new GeoLocation(1,1) ) }
      pHali.expectMsgPF() { case GameListResponse( rooms ) => {
        val id = rooms.get(0).getId
        hali.sender.s_join( id )
      }}

      pBabylon.expectMsgAllOf( 3 seconds, Joined("ephesos"), Joined("hali") )

      babylon.sender.s_startStub()

      pBabylon.expectMsgPF() { case GameBegin( state ) => {

        state.players.size === 3
        state.players.foreach( playerEmpty _ )

        val me = state.players.get(0)
        assert( me.civilisation == BABYLON_A )

        assert( state.hand.unplayables.contains( BATHS ) )

        val playables = state.hand.playables.keys.toSet
        assert( playables.contains( CLAY_PIT ) )
        assert( playables.contains( STONE_PIT ) )
        assert( playables.contains( LUMBER_YARD ) )
        assert( playables.contains( ALTAR ) )
        assert( playables.contains( THEATER ) )
        assert( playables.contains( WEST_TRADING_POST ) )

        state.hand.playables.values().foreach( v => assert( v.isEmpty ) )

        babylon.sender.s_playCard( CLAY_PIT, NO_TRADE )
      }}

      pEphesos.expectMsgAllPF[Unit](List(
        { case Connected( players ) => {
          assert( players.containsAll( List("ephesos","babylon") : JList[String] ) )
        }},
        { case Joined("hali") => () },
        { case GameBegin( state ) => {

            state.players.size === 3
            state.players.foreach( playerEmpty _ )

            assert( state.hand.unplayables.containsAll( List( WORKSHOP, STOCKADE ) ) )


            val playables = state.hand.playables

            playables.ac( CLAY_POOL )
            playables.ac( APOTHECARY, Set( Map( TAPESTRY -> List( LEFT ) ) ) )
            playables.ac( MARKETPLACE )
            playables.ac( GLASSWORKS )
            playables.ac( LOOM )

            ephesos.sender.s_playCard( GLASSWORKS, NO_TRADE )
          }
        }
      ))


      pHali.expectMsgAllPF[Unit](List(
      { case Connected( players ) => {
        assert( players.containsAll( List("ephesos","babylon","hali") : JList[String] ) )
      }},
      { case GameBegin( state ) => {
        state.players.size === 3
        state.players.foreach( playerEmpty _ )

        assert( state.hand.unplayables.containsAll( List( BARRACKS ) ) )

        val playables = state.hand.playables
        playables.ac( ORE_VEIN )
        playables.ac( GUARD_TOWER, Set( Map( CLAY -> List( LEFT ) ) ) )
        playables.ac( SCRIPTORIUM, Set( Map( PAPER -> List( RIGHT ) ) ) )
        playables.ac( EAST_TRADING_POST )
        playables.ac( PRESS )
        playables.ac( TIMBER_YARD )

        hali.sender.s_playCard( TIMBER_YARD, NO_TRADE )
      }}))

      pBabylon.expectMsgPF() { case GameUpdate( state ) => {
        val me = state.players.get(0)
        me.tableau.get(RAW_MATERIAL) === ( List(CLAY_PIT): JList[TCard] )
        me.civilisation === BABYLON_A
        me.coins === 2
        me.canPlayWonder === true

        val left = state.players.get(1)
        left.tableau.get(MANUFACTURED_GOOD) === ( List(GLASSWORKS) : JList[TCard] )
        left.civilisation === EPHESOS_B

        val right = state.players.get(2)
        right.tableau.get(RAW_MATERIAL) === ( List(TIMBER_YARD) : JList[TCard] )
        right.civilisation === HALIKARNASSOS_B
        right.coins === 2
      }}

      // STOP
    }
  }



  private val NO_TRADE = new HashMap[Resource,JList[NeighborReference]]()
  private val NO_TRADES = new java.util.HashSet[JMap[Resource,JList[NeighborReference]]]()
  NO_TRADES.add(NO_TRADE)

  private def playerEmpty( player: Player ) = {
    player.tableau.size() === 0
    player.battleMarkers === 0
    player.coins === 3
    player.score === 0
    player.wonderStaged === 0
    player.canPlayWonder === false
  }

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

  implicit class AssertPlayable( playables: JMap[TCard,JSet[JMap[Resource,JList[NeighborReference]]]] ) {
    def ac( card: TCard, trades: Set[Map[Resource,List[NeighborReference]]] = Set() ) {

      val convertTrade: JSet[JMap[Resource,JList[NeighborReference]]] =
        trades.map(
          _.map{ case ( resource, refs ) => { ( resource, refs: JList[NeighborReference] ) } } : JMap[Resource,JList[NeighborReference]]
        ): JSet[JMap[Resource,JList[NeighborReference]]]

      assert( playables.containsKey( card ) )
      playables.get( card ) === convertTrade
    }
  }
}