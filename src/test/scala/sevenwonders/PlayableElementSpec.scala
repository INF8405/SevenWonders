package sevenwonders

import org.specs2.mutable._
import com.github.jedesah.SevenWonders._
import collection.MultiSet

class PlayableElementSpec extends Specification {
  "resolve" should {
    "give coins when a reward commerce card is played" in {
      val player1 = Player(RHODOS_A, MultiSet(), 3, MultiSet(), Set(TREE_FARM), 0)
      val player2 = Player(OLYMPIA_A, MultiSet(), 3, MultiSet(), Set(MINE), 0)
      val player3 = Player(BABYLON_A, MultiSet(), 3, MultiSet(), Set(EXCAVATION), 0)
      val game = Game(List(player1, player2, player3), Map(), MultiSet())

      val newGame = VINEYARD.resolve(game, player1)
      newGame.players === List(player1.copy(coins = 6), player2, player3)
    }
    "give a fix amount of coins when a player plays a wonder like RhodosB" in {
      val player1 = Player(RHODOS_B, MultiSet(), 3, MultiSet(), Set(), 1)
      val player2 = Player(OLYMPIA_A, MultiSet(), 3, MultiSet(), Set(), 0)
      val player3 = Player(BABYLON_A, MultiSet(), 3, MultiSet(), Set(), 0)
      val game = Game(List(player1, player2, player3), Map(), MultiSet())

      val newGame = RHODOS_B.stagesOfWonder(1).resolve(game, player1)
      newGame.players === List(player1.copy(coins = 7), player2, player3)
    }
  }
}
