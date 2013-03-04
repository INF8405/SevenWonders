import org.specs2.mutable._
import com.github.jedesah.SevenWonders._

import com.sidewayscoding.Multiset

class GameSpec extends Specification with defaults {

  val defaultHand1 = Set(TAVERN, STOCKADE, MINE, LOOM, PRESS)
  val defaultPlayer1 = Player(defaultHand1.toSet, 3, Multiset(), Set(BATHS, PAWNSHOP), RHODOS)

  val defaultHand2 = Set(WEST_TRADING_POST, MARKETPLACE, EAST_TRADING_POST, BARRACKS, GUARD_TOWER)
  val defaultPlayer2 = Player(defaultHand2.toSet, 9, Multiset(), Set(), ALEXANDRIA)

  val defaultHand3 = Set(WORKSHOP, SCRIPTORIUM, THEATER, ORE_VEIN, EXCAVATION)
  val defaultPlayer3 = Player(defaultHand3.toSet, 6, Multiset(), Set(TREE_FARM), OLYMPIA)

  "A Game" should {
    "getNeighboors should return the appropriate neighboring players of any player" in {
      val game = beginGame(3)
      game.getNeighboors(game.players(0)) === Set(game.players(1), game.players(2))
      game.getNeighboors(game.players(1)) === Set(game.players(0), game.players(2))
      game.getNeighboors(game.players(2)) === Set(game.players(0), game.players(1))
    }

    "getLeftNeighboor should return the appropriate neighboring player" in {
      val game = beginGame(4)
      game.getLeftNeighboor(game.players(0)) === game.players(3)
      game.getLeftNeighboor(game.players(1)) === game.players(0)
      game.getLeftNeighboor(game.players(2)) === game.players(1)
      game.getLeftNeighboor(game.players(3)) === game.players(2)
    }

    "getRightNeighboor should return the appropriate neighboring player" in {
      val game = beginGame(5)
      game.getRightNeighboor(game.players(0)) === game.players(1)
      game.getRightNeighboor(game.players(1)) === game.players(2)
      game.getRightNeighboor(game.players(2)) === game.players(3)
      game.getRightNeighboor(game.players(3)) === game.players(4)
      game.getRightNeighboor(game.players(4)) === game.players(0)
    }

    "currentAge" in {
      val cards = classicSevenWonders.generateCards(3)
      val game = Game(List(defaultPlayer1, defaultPlayer2, defaultPlayer3), cards - 1, Multiset())
      game.currentAge === 1
    }

    "playTurn" in {
      // TODO: play an example game here
    }
  }
}
