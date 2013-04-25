package ca.polymtl.inf8405.sevenwonders.model

import SevenWonders._
import CivilizationCollection._
import CardCollection._
import Resource._

import collection.MultiSet
import collection.MultiMap
import collection.Circle

import org.specs2.mutable._

// Model/test-only ca.polymtl.inf8405.sevenwonders.model.GameSpec
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

    "current age with begin game" in {
      val game = SevenWonders.beginGame(3)
      game.currentAge ==== 1
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
        Map( 1 -> MultiSet() ,2 -> normalBaseCards(2)(3), 3 -> normalBaseCards(3)(3) )
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

      game1.findPlayer( BABYLON_A ).coins ==== 2
      game1.findPlayer( EPHESOS_B ).coins ==== 3
      assert( game1.playableCards( game1.findPlayer( EPHESOS_B ) ).contains( SCRIPTORIUM ) )
      game1.findPlayer( HALIKARNASSOS_B ).coins ==== 2

      val game2 = game1.playTurn( Map(
        game1.findPlayer( BABYLON_A ) -> Build( WORKSHOP, MultiMap( Glass -> Right ) ),
        game1.findPlayer( EPHESOS_B ) -> Build( EAST_TRADING_POST ),
        game1.findPlayer( HALIKARNASSOS_B ) -> Build( BATHS )
      ))

      game2.findPlayer( BABYLON_A ).played ==== Set( CLAY_PIT, WORKSHOP )
      val baby2 = game2.findPlayer( BABYLON_A )
      game2.playableCards( baby2 ) ==== Set(GUARD_TOWER, ORE_VEIN, PRESS, BARRACKS ) // Cannot play SCRIPTORIUM
      baby2.hand ==== MultiSet( GUARD_TOWER, ORE_VEIN, PRESS, BARRACKS, SCRIPTORIUM )

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

      val beforeScore = game3.score( game3.findPlayer( BABYLON_A ) )

      game3.findPlayer( EPHESOS_B ).played ==== Set( GLASSWORKS, EAST_TRADING_POST, WEST_TRADING_POST )
      game3.playableCards( game3.findPlayer( EPHESOS_B ) ) ==== Set( CLAY_POOL, MARKETPLACE, APOTHECARY, LOOM )

      game3.findPlayer( HALIKARNASSOS_B ).played ==== Set( TIMBER_YARD, BATHS, STOCKADE )
      game3.playableCards( game3.findPlayer( HALIKARNASSOS_B ) ) ==== Set( GUARD_TOWER, SCRIPTORIUM, ORE_VEIN, PRESS )

      val game4 = game3.playTurn( Map(
        game3.findPlayer( BABYLON_A ) -> Build( STONE_PIT, emptyTrade, wonder = true ),
        game3.findPlayer( EPHESOS_B ) -> Build( LOOM ),
        game3.findPlayer( HALIKARNASSOS_B ) -> Build( PRESS )
      ))

      val baby4 = game4.findPlayer( BABYLON_A )
      game4.playableCards( baby4 ) ==== Set( CLAY_POOL, MARKETPLACE )
      baby4.hand ==== MultiSet( CLAY_POOL, APOTHECARY, MARKETPLACE )
      baby4.played ==== Set( BARRACKS, CLAY_PIT, WORKSHOP )
      baby4.nbWonders ==== 1

      val afterScore = game4.score( game4.findPlayer( BABYLON_A ) )
      assert( beforeScore < afterScore )

      val ephesos4 = game4.findPlayer( EPHESOS_B )
      ephesos4.played ==== Set( LOOM, GLASSWORKS, EAST_TRADING_POST, WEST_TRADING_POST )
      game4.playableCards( ephesos4 ) ==== Set( SCRIPTORIUM, GUARD_TOWER, ORE_VEIN )
      ephesos4.coins ==== 5

      game4.findPlayer( HALIKARNASSOS_B ).played ==== Set( TIMBER_YARD, BATHS, STOCKADE, PRESS )
      game4.playableCards( game4.findPlayer( HALIKARNASSOS_B ) ) ==== Set(ALTAR, THEATER, LUMBER_YARD )

      val game5 = game4.playTurn( Map(
        game4.findPlayer( BABYLON_A ) -> Build( MARKETPLACE ),
        game4.findPlayer( EPHESOS_B ) -> Discard( ORE_VEIN ),
        game4.findPlayer( HALIKARNASSOS_B ) -> Discard( LUMBER_YARD )
      ))

      game5.discarded ==== MultiSet( ORE_VEIN, LUMBER_YARD )
      game5.findPlayer( EPHESOS_B ).coins ==== 8
      game5.findPlayer( HALIKARNASSOS_B  ).coins ==== 5

      val game_age2_1 = game5.playTurn( Map(
        game5.findPlayer( BABYLON_A ) -> Build( GUARD_TOWER ),
        game5.findPlayer( EPHESOS_B ) -> Build( ALTAR ),
        game5.findPlayer( HALIKARNASSOS_B ) -> Build( APOTHECARY )
      ))

      game_age2_1.discarded ==== MultiSet(ORE_VEIN, LUMBER_YARD, CLAY_POOL, THEATER, SCRIPTORIUM)

      val baby_2_1 = game_age2_1.findPlayer( BABYLON_A )
      val ephesos_2_1 = game_age2_1.findPlayer( EPHESOS_B )
      val hali_2_1 = game_age2_1.findPlayer( HALIKARNASSOS_B )

      baby_2_1.stuff ==== MultiSet(VictoryBattleMarker(1), VictoryBattleMarker(1))
      ephesos_2_1.stuff ==== MultiSet(new DefeatBattleMarker, new DefeatBattleMarker)
      hali_2_1.stuff ==== MultiSet(new DefeatBattleMarker, VictoryBattleMarker(1))

      val baby_2_1mod = baby_2_1.copy( hand = MultiSet( CARAVANSERY, VINEYARD, STATUE, ARCHERY_RANGE, DISPENSARY, WALLS, FOUNDRY ) )
      val ephesos_2_1mod = ephesos_2_1.copy( hand = MultiSet( LABORATORY, LIBRARY, STABLES, TEMPLE, AQUEDUCT, COURTHOUSE, FORUM ) )
      val hali_2_1mod = hali_2_1.copy( hand = MultiSet( SCHOOL, GLASSWORKS, BRICKYARD, LOOM, QUARRY, SAWMILL, PRESS ) )

      val game_age2_1mod = game_age2_1.copy( players = new Circle[Player]( baby_2_1mod, ephesos_2_1mod, hali_2_1mod ) )

//      val game_age2_2 = game_age2_1mod.playTurn( Map(
//        game_age2_1mod.findPlayer( BABYLON_A ) -> Build( ),
//        game_age2_1mod.findPlayer( EPHESOS_B ) -> Build( ),
//        game_age2_1mod.findPlayer( HALIKARNASSOS_B ) -> Build( )
//      ))
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

    "ressource when neighbor has it" in {
      val hand1 = MultiSet[Card]( WEST_TRADING_POST, THEATER, ALTAR, LUMBER_YARD, GUARD_TOWER, STONE_PIT )
      val gizah = Player( civilization = GIZAH_A, hand = hand1, coins = 3 , played = Set(CLAY_POOL))

      val hand2 = MultiSet[Card]( CLAY_PIT, APOTHECARY, WORKSHOP, MARKETPLACE, STOCKADE, GLASSWORKS)
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