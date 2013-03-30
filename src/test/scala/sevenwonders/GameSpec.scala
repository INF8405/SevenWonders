package sevenwonders

import org.specs2.mutable._
import com.github.jedesah.SevenWonders._

import collection.MultiSet

class GameSpec extends Specification {

  val defaultHand1: MultiSet[Card] = MultiSet(TAVERN, STOCKADE, MINE, LOOM, PRESS)
  val defaultPlayer1 = Player(RHODOS_A, defaultHand1, 3, MultiSet(), Set(BATHS, PAWNSHOP), 0)

  val defaultHand2: MultiSet[Card] = MultiSet(WEST_TRADING_POST, MARKETPLACE, EAST_TRADING_POST, BARRACKS, GUARD_TOWER)
  val defaultPlayer2 = Player(ALEXANDRIA_A, defaultHand2, 9, MultiSet(), Set(), 0)

  val defaultHand3: MultiSet[Card] = MultiSet(WORKSHOP, SCRIPTORIUM, THEATER, ORE_VEIN, EXCAVATION)
  val defaultPlayer3 = Player(OLYMPIA_A, defaultHand3, 6, MultiSet(), Set(TREE_FARM), 0)

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
      val game = Game(List(defaultPlayer1, defaultPlayer2, defaultPlayer3), cards.updated(1, MultiSet()), MultiSet())
      game.currentAge === 1
    }

    "playTurn" in {
      // TODO: play an example game here
      pending
    }
  }
}
