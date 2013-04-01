package sevenwonders

import org.specs2.mutable._
import com.github.jedesah.SevenWonders._
import collection.MultiSet

class CoinSymbolSpec extends Specification {
  "resolve" should {
    "variableReward" in {
      val player1 = Player(RHODOS_A, MultiSet(), 3, MultiSet(), Set(TREE_FARM), 0)
      val player2 = Player(OLYMPIA_A, MultiSet(), 3, MultiSet(), Set(MINE), 0)
      val player3 = Player(BABYLON_A, MultiSet(), 3, MultiSet(), Set(EXCAVATION), 0)
      val game = Game(List(player1, player2, player3), Map(), MultiSet())

      val newGame = CoinSymbol(VariableReward(1, classOf[RawMaterialCard], Set(Left, Right, Self))).resolve(game, player1)
      newGame.players === List(player1.addCoins(3), player2, player3)
    }
    "SimpleReward" in {
      val player1 = Player(RHODOS_B, MultiSet(), 3, MultiSet(), Set(), 1)
      val player2 = Player(OLYMPIA_A, MultiSet(), 3, MultiSet(), Set(), 0)
      val player3 = Player(BABYLON_A, MultiSet(), 3, MultiSet(), Set(), 0)
      val game = Game(List(player1, player2, player3), Map(), MultiSet())

      val newGame = CoinSymbol(SimpleReward(4)).resolve(game, player1)
      newGame.players === List(player1.addCoins(4), player2, player3)
    }
    "ThreeWayReward" in {
      val player1 = Player(HALIKARNASSOS_A, MultiSet(), 3)
      val player2 = Player(GIZAH_A)
      val player3 = Player(RHODOS_A, MultiSet(), 7)
      val player4 = Player(OLYMPIA_A)
      val game = Game(List(player1, player2, player3, player4), Map(), MultiSet())

      val newGame = CoinSymbol(ThreeWayReward(2, 9, 2)).resolve(game, player2)
      newGame.players === List(player1.addCoins(2), player2.addCoins(9), player3.addCoins(2), player4)
    }
  }
}
