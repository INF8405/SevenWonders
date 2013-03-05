import org.specs2.mutable._
import com.github.jedesah.SevenWonders._

import com.sidewayscoding.Multiset

trait defaults {
  val defaultHand = Set(TAVERN, STOCKADE, MINE, LOOM, PRESS)
  val defaultPlayer = Player(defaultHand.toSet, 3, Multiset(), Set(BATHS, PAWNSHOP), OLYMPIA)
}

class PlayerSpec extends Specification with defaults {

  "A Player" should {
    "receive 3 coins when they discard a card and it should be removed from their hand" in {
      defaultPlayer.discard(PRESS) === Player((defaultHand - PRESS).toSet, 6, Multiset(), Set(BATHS, PAWNSHOP), OLYMPIA)
    }

    "playing a card should:" +
      "- remove the card from their hand," +
      "- add the card to their playedCards, +" +
      "- substract the coin cost and" +
      "- add any coin value supplied by the played card" in {
      defaultPlayer.play(TAVERN, Map()) === Player((defaultHand - TAVERN).toSet, 7, Multiset(), Set(BATHS, ALTAR, TAVERN), OLYMPIA)
    }

    "playableCards should return the Set of Cards it is possible for the player to play for his next turn" in {
      defaultPlayer.playableCards(Map()) === Set(TAVERN, MINE, LOOM, PRESS)
    }

    "cannot play a card he already has" in {
      val player = Player(defaultHand.toSet, 3, Multiset(), Set(BATHS, ALTAR, TAVERN), OLYMPIA)
      player.playableCards(Map()) === Set(MINE, LOOM, PRESS)
    }

    "can play a card by trading a resource with a neighboor" in {
      defaultPlayer.playableCards(Map(Left -> Wood)) === defaultHand
    }

    "total production" in {
      val player = Player(Set(THEATER, BATHS, ALTAR, PAWNSHOP), 3, Multiset(), Set(MINE, CLAY_PIT, GLASSWORKS), OLYMPIA)
      player.totalProduction === (Stone + Clay + Glass | Stone + Ore + Glass | Ore + Clay + Glass | Ore + Ore + Glass)
    }

    "tradableProductions; A player cannot trade a production that is granted from a Commerce Card" in {
      val player = Player(Set(AQUEDUCT, STATUE, SCHOOL, LIBRARY, LABORATORY, STABLES),
        3,
        Multiset(VictoryBattleMarker(1), VictoryBattleMarker(1)),
        Set(STOCKADE, BARRACKS, GUARD_TOWER, TREE_FARM, MINE, CLAY_POOL, CARAVANSERY),
        OLYMPIA)

      player.totalProduction === (Wood + Stone + Clay + Wood |
      Wood + Stone + Clay + Stone |
      Wood + Stone + Clay + Ore |
      Wood + Stone + Clay + Clay |
      Wood + Ore + Clay + Wood |
      Wood + Ore + Clay + Stone |
      Wood + Ore + Clay + Ore |
      Wood + Ore + Clay + Clay |
      Clay + Stone + Clay + Wood |
      Clay + Stone + Clay + Stone |
      Clay + Stone + Clay + Ore |
      Clay + Stone + Clay + Clay |
      Clay + Ore + Clay + Wood |
      Clay + Ore + Clay + Stone |
      Clay + Ore + Clay + Ore |
      Clay + Ore + Clay + Clay)

      player.tradableProduction === (Wood + Stone + Clay | Wood + Ore + Clay | Clay + Stone + Clay | Clay + Ore + Clay)
    }

    "score" in {
      defaultPlayer.score(Map()) === 5
    }
  }
}
