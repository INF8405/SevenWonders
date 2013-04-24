package ca.polymtl.inf8405.sevenwonders.model

import SevenWonders._
import CivilizationCollection._
import CardCollection._
import Resource._

import collection.MultiSet
import collection.MultiMap
import collection.Circle

import org.specs2.mutable._

// test-only ca.polymtl.inf8405.sevenwonders.model.GameSpec
class GameSpec extends Specification {

  val defaultHand1: MultiSet[Card] = MultiSet(TAVERN, STOCKADE, MINE, LOOM, PRESS)
  val defaultPlayer1 = Player(RHODOS_A, defaultHand1, 3, MultiSet(), Set(BATHS, PAWNSHOP), 0)

  val defaultHand2: MultiSet[Card] = MultiSet(WEST_TRADING_POST, MARKETPLACE, EAST_TRADING_POST, BARRACKS, GUARD_TOWER)
  val defaultPlayer2 = Player(ALEXANDRIA_A, defaultHand2, 9, MultiSet(), Set(), 0)

  val defaultHand3: MultiSet[Card] = MultiSet(WORKSHOP, SCRIPTORIUM, THEATER, ORE_VEIN, EXCAVATION)
  val defaultPlayer3 = Player(OLYMPIA_A, defaultHand3, 6, MultiSet(), Set(TREE_FARM), 0)

  "A Game" should {
    "currentAge" in {
      val cards = classicSevenWonders.generateCards(3)
      val game = Game(new Circle(defaultPlayer1, defaultPlayer2, defaultPlayer3), cards.updated(1, MultiSet()), MultiSet())
      game.currentAge === 1
    }

    "bug1" in {
      val hand1 = MultiSet[Card]( WEST_TRADING_POST, THEATER, ALTAR, LUMBER_YARD, BATHS, STONE_PIT, CLAY_PIT )
      val babylon = Player( civilization = BABYLON_A, hand = hand1, coins = 3 )

      val hand2 = MultiSet[Card]( CLAY_POOL, APOTHECARY, WORKSHOP, MARKETPLACE, STOCKADE, GLASSWORKS, LOOM )
      val ephesos = Player( civilization = EPHESOS_B, hand = hand2, coins = 3 )

      val hand3 = MultiSet[Card]( TIMBER_YARD, PRESS, EAST_TRADING_POST, BARRACKS, SCRIPTORIUM, GUARD_TOWER, ORE_VEIN )
      val hali = Player( civilization = HALIKARNASSOS_B, hand = hand3, coins = 3 )

      val game = Game(
        new Circle[Player]( babylon, ephesos, hali ),
        Map( 1 -> MultiSet(DUMMY_CARD), 2 -> MultiSet(DUMMY_CARD) )
      )

      game.players.getLeft(ephesos) ==== babylon

      val actual = game.possibleTrades(ephesos, APOTHECARY)
      val expected = Set(MultiMap[Resource, NeighborReference](Tapestry -> Right))
      actual ==== expected

      val game1 = game.playTurn( Map(
        babylon -> Build( CLAY_PIT ),
        ephesos -> Build( GLASSWORKS ),
        hali -> Build( TIMBER_YARD )
      ))

      val game2 = game1.playTurn( Map(
        game1.findPlayer( BABYLON_A ) -> Build( WORKSHOP, MultiMap( Glass -> Right ) ),
        game1.findPlayer( EPHESOS_B ) -> Build( EAST_TRADING_POST ),
        game1.findPlayer( HALIKARNASSOS_B ) -> Build( BATHS )
      ))

      game2.findPlayer( BABYLON_A ).played ==== Set( CLAY_PIT, WORKSHOP )
      game2.playableCards( game2.findPlayer( BABYLON_A ) ) ==== Set(GUARD_TOWER, ORE_VEIN, PRESS, BARRACKS)

      game2.findPlayer( EPHESOS_B ).played ==== Set( GLASSWORKS, EAST_TRADING_POST )
      game2.playableCards( game2.findPlayer( EPHESOS_B ) ) ==== Set(ALTAR, THEATER, STONE_PIT, LUMBER_YARD, WEST_TRADING_POST)

      game2.findPlayer( HALIKARNASSOS_B ).played ==== Set( TIMBER_YARD, BATHS )
      game2.playableCards( game2.findPlayer( HALIKARNASSOS_B ) ) ==== Set( CLAY_POOL, MARKETPLACE, APOTHECARY, LOOM, STOCKADE )

      val game3 = game2.playTurn( Map(
        game2.findPlayer( BABYLON_A ) -> Build( BARRACKS ),
        game2.findPlayer( EPHESOS_B ) -> Build( WEST_TRADING_POST ),
        game2.findPlayer( HALIKARNASSOS_B ) -> Build( STOCKADE )
      ))

      game3.findPlayer( BABYLON_A ).played ==== Set( CLAY_PIT, WORKSHOP, BARRACKS )
      game3.playableCards( game3.findPlayer( BABYLON_A ) ) ==== Set( ALTAR, THEATER, STONE_PIT, LUMBER_YARD )

      game3.findPlayer( EPHESOS_B ).played ==== Set( GLASSWORKS, EAST_TRADING_POST, WEST_TRADING_POST )
      game3.playableCards( game3.findPlayer( EPHESOS_B ) ) ==== Set( CLAY_POOL, MARKETPLACE, APOTHECARY, LOOM )

      game3.findPlayer( HALIKARNASSOS_B ).played ==== Set( TIMBER_YARD, BATHS, STOCKADE )
      game3.playableCards( game3.findPlayer( HALIKARNASSOS_B ) ) ==== Set( GUARD_TOWER, SCRIPTORIUM, ORE_VEIN, PRESS )
    }

    "optionnal ressource when neighbor has it" in {
      val hand1 = MultiSet[Card]( WEST_TRADING_POST, THEATER, ALTAR, LUMBER_YARD, GUARD_TOWER, STONE_PIT )
      val gizah = Player( civilization = GIZAH_A, hand = hand1, coins = 3 , played = Set(CLAY_PIT))

      val hand2 = MultiSet[Card]( CLAY_POOL, APOTHECARY, WORKSHOP, MARKETPLACE, STOCKADE, GLASSWORKS)
      val baby = Player( civilization = BABYLON_A, hand = hand2, coins = 3, played = Set(LOOM) )

      val hand3 = MultiSet[Card]( TIMBER_YARD, ORE_VEIN, EAST_TRADING_POST, BATHS, SCRIPTORIUM, BARRACKS)
      val hali = Player( civilization = HALIKARNASSOS_B, hand = hand3, coins = 3, played = Set(PRESS) )

      val game = Game(
        new Circle[Player]( gizah, baby, hali ),
        Map( 1 -> MultiSet(DUMMY_CARD), 2 -> MultiSet(DUMMY_CARD) )
      )

      game.possibleTrades(gizah, GUARD_TOWER) ==== Set.empty
    }
  }
}