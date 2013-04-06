package sevenwonders

import collection.MultiMap
import org.specs2.mutable._
import com.github.jedesah.SevenWonders._

import collection.MultiSet

trait defaults {
  val defaultHand: MultiSet[Card] = MultiSet(TAVERN, STOCKADE, MINE, LOOM, PRESS)
  val defaultPlayer = Player(OLYMPIA_A, defaultHand, 3, MultiSet(), Set(BATHS, PAWNSHOP))
}

class PlayerSpec extends Specification with defaults {

  "A Player" should {
    "receive 3 coins when they discard a card and it should be removed from their hand" in {
      defaultPlayer.discard(PRESS) === Player(OLYMPIA_A, defaultHand - PRESS, 6, MultiSet(), Set(BATHS, PAWNSHOP), 0)
    }

    // n.b. We don't resolve the effect of the card (usually receiving coins or something like that here)
    // because we do it in game. This is due to the fact that we must resolve cards after everyone has played
    "playing a card" should {
      "remove the card from their hand" in {
        defaultPlayer.build(TAVERN, MultiMap())._1.hand === (defaultHand - TAVERN)
      }
      "add the card to their playedCards" in {
        defaultPlayer.build(TAVERN, MultiMap())._1.played === Set(BATHS, PAWNSHOP, TAVERN)
      }
      "substract the coin cost" in {
        defaultPlayer.build(MINE, MultiMap())._1.coins === 2
      }
      "substract the trade cost" in {
        val hand = MultiSet[Card](THEATER, BATHS, ALTAR, WORKSHOP)
        val player = Player(OLYMPIA_A, hand, 3, MultiSet(), Set(PAWNSHOP), 0)
        val (newPlayer, map) = player.build(BATHS, MultiMap(Stone -> Left))
        newPlayer === Player(OLYMPIA_A, hand - BATHS, 1, MultiSet(), Set(PAWNSHOP, BATHS), 0)
        map === Map(Left -> 2)
      }
    }

    "buildForFree" in {
      pending
    }

    "canBuildForFree" in {
      pending
    }

    "addCoins" in {
      Player(GIZAH_A).addCoins(3) === Player(civilization = GIZAH_A, coins = 3)
      Player(civilization = OLYMPIA_A, coins = 5).addCoins(4) === Player(civilization = OLYMPIA_A, coins = 9)
    }

    "playableCards should return the Set of Cards it is possible for the player to build for his next turn" should {
      "be able to build all cards if he has all the required resources" in {
        defaultPlayer.playableCards(Map()) === defaultHand.toSet
      }
      "be unable to build all cards if he does not have all the required resources" in {
        val player = Player(GIZAH_A, defaultHand, 3, MultiSet(), Set(BATHS, ALTAR), 0)
        player.playableCards(Map()) === Set(MINE, LOOM, PRESS, TAVERN)
      }
      "be unable to build a card requiring coins if he does not have any coins" in {
        val player = Player(GIZAH_A, defaultHand, 0, MultiSet(), Set(BATHS, ALTAR), 0)
        player.playableCards(Map()) === Set(LOOM, PRESS, TAVERN)
      }
      "cannot build a card he already has" in {
        val player = Player(GIZAH_A, defaultHand, 3, MultiSet(), Set(BATHS, ALTAR, TAVERN), 0)
        player.playableCards(Map()) === Set(MINE, LOOM, PRESS)
      }
      "can build a card by trading a resource with a neighboor" in {
        val player = Player(GIZAH_A, defaultHand, 3, MultiSet(), Set(BATHS, ALTAR), 0)
        player.playableCards(Map(Left -> Wood)) === defaultHand.toSet
      }
      "can build all cards if he has the symbol can build one free per age and has not done so yet" in {
        val hand = MultiSet[Card](SCHOOL, WALLS, BRICKYARD, COURTHOUSE, AQUEDUCT, STABLES)
        val player = Player(OLYMPIA_A, hand, 0, MultiSet(), Set(), 2)
        player.playableCards(Map()) === hand.toSet
      }
      "cannot build all cards if he has the symbol can build one free per age but has allready used it" in {
        val hand = MultiSet[Card](SCHOOL, WALLS, BRICKYARD, COURTHOUSE, AQUEDUCT, STABLES)
        val player = Player(OLYMPIA_A, hand, 0, MultiSet(), Set(), 2, true)
        player.playableCards(Map()) === Set()
      }
    }

    "canBuild" in {
      defaultPlayer.canBuild(TAVERN, Map()) === true
      val player = Player(GIZAH_A, defaultHand, 3, MultiSet(), Set(BATHS, ALTAR, TAVERN), 0)
      player.canBuild(TAVERN, Map()) === false
    }

    "possibleTrades" in {
      defaultPlayer.possibleTrades(TAVERN, Map[NeighborReference, Production]()) === Set(MultiMap())
    }

    "total production" in {
      val player = Player(OLYMPIA_A, MultiSet(THEATER, BATHS, ALTAR, PAWNSHOP), 3, MultiSet(), Set(MINE, CLAY_PIT, GLASSWORKS), 0)
      player.totalProduction === (Stone + Clay + Glass + Wood | Stone + Ore + Glass + Wood | Ore + Clay + Glass + Wood | Ore + Ore + Glass + Wood)
    }

    "tradableProductions; A player cannot trade a production that is granted from a Commerce Card" in {
      val player = Player(GIZAH_A, MultiSet(AQUEDUCT), 3, MultiSet(), Set(CLAY_POOL, FORUM), 0)
      player.totalProduction === (Stone + Clay + Paper | Stone + Clay + Tapestry | Stone + Clay + Glass)
      player.tradableProduction === (Stone + Clay)

      val player1 = Player(
        OLYMPIA_A,
        MultiSet(AQUEDUCT, STATUE, SCHOOL, LIBRARY, LABORATORY, STABLES),
        3,
        MultiSet(VictoryBattleMarker(1), VictoryBattleMarker(1)),
        Set(STOCKADE, BARRACKS, GUARD_TOWER, TREE_FARM, CARAVANSERY),
        0
      )

      player1.totalProduction === ((Wood + Wood + Wood) |
        (Wood + Stone + Wood) |
        (Wood + Clay + Wood) |
        (Wood + Ore + Wood) |
        (Clay + Wood + Wood) |
        (Clay + Stone + Wood) |
        (Clay + Clay + Wood) |
        (Clay + Ore + Wood))

      player1.tradableProduction  === (Wood + Wood | Clay + Wood)

      val player2 = Player(
        OLYMPIA_A,
        MultiSet(AQUEDUCT, STATUE, SCHOOL, LIBRARY, LABORATORY, STABLES),
        3,
        MultiSet(VictoryBattleMarker(1), VictoryBattleMarker(1)),
        Set(STOCKADE, BARRACKS, GUARD_TOWER, TREE_FARM, MINE, CLAY_POOL, CARAVANSERY),
        0
      )

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

    "allSymbols" in {
      val player = Player(OLYMPIA_A, MultiSet(), 3, MultiSet(), Set(BATHS, AQUEDUCT, STABLES),1)
      player.allSymbols ==== MultiSet(
        VictoryPointSymbol(SimpleAmount(3)),
        VictoryPointSymbol(SimpleAmount(3)),
        VictoryPointSymbol(SimpleAmount(5)),
        MilitarySymbol(2)
      )
    }

    "allPlayables" in {
      val player = Player(OLYMPIA_A, MultiSet(), 3, MultiSet(), Set(BATHS), 1)
      player.allPlayables ==== MultiSet(
        BATHS,
        OLYMPIA_A.stagesOfWonder(0)
      )
    }

    "allGameElements" in {
      val player = Player(OLYMPIA_A, MultiSet(), 3, MultiSet(VictoryBattleMarker(1), new DefeatBattleMarker), Set(BATHS), 1)
      player.allGameElements ==== MultiSet(
        BATHS,
        OLYMPIA_A.stagesOfWonder(0),
        VictoryBattleMarker(1),
        new DefeatBattleMarker
      )
    }

    "militaryStrength" in {
      val player = Player(RHODOS_B, MultiSet(), 3, MultiSet(VictoryBattleMarker(1)), Set(STOCKADE, STABLES, FORTIFICATIONS), 2)
      player.militaryStrength === 8
    }

    "score" in {
      defaultPlayer.score(Map()) === 7
    }

    "scienceScore" in {
      val player = Player(
        BABYLON_A,
        MultiSet(),
        3,
        MultiSet(new DefeatBattleMarker, new DefeatBattleMarker),
        Set(WORKSHOP, SCRIPTORIUM, APOTHECARY, SCHOOL, DISPENSARY, LIBRARY, SCIENTISTS_GUILD),
        2
      )
      player.scienceScore() === 38
    }

    "militaryScore" in {
      val player = Player(
        HALIKARNASSOS_A,
        MultiSet(),
        3,
        MultiSet(
          new DefeatBattleMarker,
          new DefeatBattleMarker,
          new VictoryBattleMarker(1),
          new VictoryBattleMarker(3),
          new VictoryBattleMarker(5)
        )
      )
      player.militaryScore === 7
    }

    "civilianScore" in {
      defaultPlayer.civilianScore === 6
    }

    "commerceScore" in {
      val player = Player(
        GIZAH_B,
        MultiSet(),
        3,
        MultiSet(),
        Set(
          LIGHTHOUSE, CHAMBER_OF_COMMERCE, ARENA, HAVEN, PRESS, LOOM, VINEYARD, BRICKYARD
        ),
        4
      )
      player.commerceScore === 5 + 4 + 4 + 1
    }
  }
}
