package ca.polymtl.inf8405.sevenwonders.model

import org.specs2.mutable._
import SevenWonders._
import collection.MultiSet
import collection.Circle

class CoinSymbolSpec extends Specification {
  "resolve" should {
    "variableReward" in {
      val player1 = Player(RHODOS_A, MultiSet(), 3, MultiSet(), Set(TREE_FARM), 0)
      val player2 = Player(OLYMPIA_A, MultiSet(), 3, MultiSet(), Set(MINE), 0)
      val player3 = Player(BABYLON_A, MultiSet(), 3, MultiSet(), Set(EXCAVATION), 0)
      val game = Game(new Circle(player1, player2, player3), Map(), MultiSet())

      val newGame = CoinSymbol(VariableAmount(1, classOf[RawMaterialCard], Set(Left, Right, Self))).resolve(game, player1)
      newGame.players === new Circle(player1.addCoins(3), player2, player3)
    }
    "SimpleAmount" in {
      val player1 = Player(RHODOS_B, MultiSet(), 3, MultiSet(), Set(), 1)
      val player2 = Player(OLYMPIA_A, MultiSet(), 3, MultiSet(), Set(), 0)
      val player3 = Player(BABYLON_A, MultiSet(), 3, MultiSet(), Set(), 0)
      val game = Game(new Circle(player1, player2, player3), Map(), MultiSet())

      val newGame = CoinSymbol(SimpleAmount(4)).resolve(game, player1)
      newGame.players === new Circle(player1.addCoins(4), player2, player3)
    }
    "ThreeWayAmount" in {
      val player1 = Player(HALIKARNASSOS_A, MultiSet(), 3)
      val player2 = Player(GIZAH_A)
      val player3 = Player(RHODOS_A, MultiSet(), 7)
      val player4 = Player(OLYMPIA_A)
      val game = Game(new Circle(player1, player2, player3, player4), Map(), MultiSet())

      val newGame = CoinSymbol(ThreeWayAmount(2, 9, 2)).resolve(game, player2)
      newGame.players === new Circle(player1.addCoins(2), player2.addCoins(9), player3.addCoins(2), player4)
    }
  }
}
