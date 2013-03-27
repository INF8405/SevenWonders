package sevenwonders

import collection.MultiMap
import org.specs2.mutable._
import com.github.jedesah.SevenWonders._

import collection.MultiSet

trait defaults {
  val defaultHand: MultiSet[Card] = MultiSet(TAVERN, STOCKADE, MINE, LOOM, PRESS)
  val defaultPlayer = Player(defaultHand, 3, MultiSet(), Set(BATHS, PAWNSHOP), OLYMPIA)
}

class PlayerSpec extends Specification with defaults {

  "A Player" should {
    "receive 3 coins when they discard a card and it should be removed from their hand" in {
      defaultPlayer.discard(PRESS) === Player(defaultHand - PRESS, 6, MultiSet(), Set(BATHS, PAWNSHOP), OLYMPIA)
    }

    // n.b. We don't resolve the effect of the card (usually receiving coins or something like that here)
    // because we do it in game. This is due to the fact that we must resolve cards after everyone has played
    "playing a card" should {
      "remove the card from their hand" in {
        defaultPlayer.play(TAVERN, MultiMap())._1.hand === (defaultHand - TAVERN)
      }
      "add the card to their playedCards" in {
        defaultPlayer.play(TAVERN, MultiMap())._1.played === Set(BATHS, PAWNSHOP, TAVERN)
      }
      "substract the coin cost" in {
        defaultPlayer.play(MINE, MultiMap())._1.coins === 2
      }
    }

    "playableCards should return the Set of Cards it is possible for the player to play for his next turn" in {
      defaultPlayer.playableCards(Map()) === Set(TAVERN, MINE, LOOM, PRESS)
    }

    "cannot play a card he already has" in {
      val player = Player(defaultHand, 3, MultiSet(), Set(BATHS, ALTAR, TAVERN), GIZAH)
      player.playableCards(Map()) === Set(MINE, LOOM, PRESS)
    }

    "can play a card by trading a resource with a neighboor" in {
      defaultPlayer.playableCards(Map(Left -> Wood)) === defaultHand
    }

    "canPlayCard" in {
      defaultPlayer.canPlayCard(TAVERN, Map()) === true
      val player = Player(defaultHand, 3, MultiSet(), Set(BATHS, ALTAR, TAVERN), GIZAH)
      player.canPlayCard(TAVERN, Map()) === false
    }

    "possibleTrades" in {
      defaultPlayer.possibleTrades(TAVERN, Map[NeighboorReference, Production]()) === Set(MultiMap())
    }

    "total production" in {
      val player = Player(MultiSet(THEATER, BATHS, ALTAR, PAWNSHOP), 3, MultiSet(), Set(MINE, CLAY_PIT, GLASSWORKS), OLYMPIA)
      player.totalProduction === (Stone + Clay + Glass + Wood | Stone + Ore + Glass + Wood | Ore + Clay + Glass + Wood | Ore + Ore + Glass + Wood)
    }

    "tradableProductions; A player cannot trade a production that is granted from a Commerce Card" in {
      val player = Player(MultiSet(AQUEDUCT), 3, MultiSet(), Set(CLAY_POOL, FORUM), GIZAH)
      player.totalProduction === (Stone + Clay + Paper | Stone + Clay + Tapestry | Stone + Clay + Glass)
      player.tradableProduction === (Stone + Clay)

      val player1 = Player(MultiSet(AQUEDUCT, STATUE, SCHOOL, LIBRARY, LABORATORY, STABLES),
        3,
        MultiSet(VictoryBattleMarker(1), VictoryBattleMarker(1)),
        Set(STOCKADE, BARRACKS, GUARD_TOWER, TREE_FARM, CARAVANSERY),
        OLYMPIA)

      player1.totalProduction === ((Wood + Wood + Wood) |
        (Wood + Stone + Wood) |
        (Wood + Clay + Wood) |
        (Wood + Ore + Wood) |
        (Clay + Wood + Wood) |
        (Clay + Stone + Wood) |
        (Clay + Clay + Wood) |
        (Clay + Ore + Wood))

      player1.tradableProduction  === (Wood + Wood | Clay + Wood)

      val player2 = Player(MultiSet(AQUEDUCT, STATUE, SCHOOL, LIBRARY, LABORATORY, STABLES),
        3,
        MultiSet(VictoryBattleMarker(1), VictoryBattleMarker(1)),
        Set(STOCKADE, BARRACKS, GUARD_TOWER, TREE_FARM, MINE, CLAY_POOL, CARAVANSERY),
        OLYMPIA)

      player2.totalProduction === (Wood + Stone + Clay + Wood + Wood |
        Wood + Stone + Clay + Stone + Wood |
        Wood + Stone + Clay + Ore + Wood |
        Wood + Stone + Clay + Clay + Wood |
        Wood + Ore + Clay + Wood + Wood |
        Wood + Ore + Clay + Stone + Wood |
        Wood + Ore + Clay + Ore + Wood |
        Wood + Ore + Clay + Clay + Wood |
        Clay + Stone + Clay + Wood + Wood |
        Clay + Stone + Clay + Stone + Wood |
        Clay + Stone + Clay + Ore + Wood |
        Clay + Stone + Clay + Clay + Wood |
        Clay + Ore + Clay + Wood + Wood |
        Clay + Ore + Clay + Stone + Wood |
        Clay + Ore + Clay + Ore + Wood |
        Clay + Ore + Clay + Clay + Wood)

      player2.tradableProduction === (Wood + Stone + Clay + Wood |
        Wood + Ore + Clay + Wood |
        Clay + Stone + Clay + Wood |
        Clay + Ore + Clay + Wood)
    }

    "score" in {
      defaultPlayer.score(Map()) === 6
    }

    "civilianScore" in {
      defaultPlayer.score(Map()) === 6
    }
  }
}
