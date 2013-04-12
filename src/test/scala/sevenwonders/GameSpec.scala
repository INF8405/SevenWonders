package ca.polymtl.inf8405.sevenwonders.model

import org.specs2.mutable._
import SevenWonders._

import collection.MultiSet
import collection.Circle

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

    "playTurn" in {
      // TODO: play an example game here
      pending
    }
  }
}
