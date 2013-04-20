package ca.polymtl.inf8405.sevenwonders.model

import collection.MultiSet

import Resource._

object CivilizationCollection {

  // Civilizations
  val RHODOS_A = Civilization("RHODOS_A", Ore, List(
    WonderStage(Cost(0, MultiSet(Wood, Wood)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Clay, Clay, Clay)), Set(MilitarySymbol(2))),
    WonderStage(Cost(0, MultiSet(Ore, Ore, Ore, Ore)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val RHODOS_B = Civilization("RHODOS_B", Ore, List(
    WonderStage(Cost(0, MultiSet(Stone, Stone, Stone)), Set(MilitarySymbol(1), VictoryPointSymbol(SimpleAmount(3)), CoinSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Ore, Ore, Ore, Ore)), Set(MilitarySymbol(1), VictoryPointSymbol(SimpleAmount(4)), CoinSymbol(SimpleAmount(4))))
  ))
  val ALEXANDRIA_A = Civilization("ALEXANDRIA_A", Glass, List(
    WonderStage(Cost(0, MultiSet(Stone, Stone)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Ore, Ore)), Set(Clay | Ore | Wood | Stone)),
    WonderStage(Cost(0, MultiSet(Glass, Glass)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val ALEXANDRIA_B = Civilization("ALEXANDRIA_B", Glass, List(
    WonderStage(Cost(0, MultiSet(Clay, Clay)), Set(Wood | Stone | Ore | Clay)),
    WonderStage(Cost(0, MultiSet(Wood, Wood)), Set(Glass | Tapestry | Paper)),
    WonderStage(Cost(0, MultiSet(Stone, Stone)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val HALIKARNASSOS_A = Civilization("HALIKARNASSOS_A", Tapestry, List(
    WonderStage(Cost(0, MultiSet(Clay, Clay)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Ore, Ore, Ore)), Set(GrabFromDiscardPile)),
    WonderStage(Cost(0, MultiSet(Tapestry, Tapestry)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val HALIKARNASSOS_B = Civilization("HALIKARNASSOS_B", Tapestry, List(
    WonderStage(Cost(0, MultiSet(Ore, Ore)), Set(VictoryPointSymbol(SimpleAmount(2)), GrabFromDiscardPile)),
    WonderStage(Cost(0, MultiSet(Clay, Clay, Clay)), Set(VictoryPointSymbol(SimpleAmount(1)), GrabFromDiscardPile)),
    WonderStage(Cost(0, MultiSet(Glass, Paper, Tapestry)), Set(GrabFromDiscardPile))
  ))
  val OLYMPIA_A = Civilization("OLYMPIA_A", Wood, List(
    WonderStage(Cost(0, MultiSet(Wood, Wood)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Stone, Stone)), Set(FreeBuildEachAge)),
    WonderStage(Cost(0, MultiSet(Ore, Ore)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val OLYMPIA_B = Civilization("OLYMPIA_B", Wood, List(
    WonderStage(Cost(0, MultiSet(Wood, Wood)), Set(RebateSymbol(Set(Clay, Stone, Wood, Ore), Set(Left, Right)))),
    WonderStage(Cost(0, MultiSet(Stone, Stone)), Set(VictoryPointSymbol(SimpleAmount(5)))),
    WonderStage(Cost(0, MultiSet(Tapestry, Ore, Ore)), Set(CopyGuildCard))
  ))
  val GIZAH_A = Civilization("GIZAH_A", Stone, List(
    WonderStage(Cost(0, MultiSet(Stone, Stone)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Wood, Wood, Wood)), Set(VictoryPointSymbol(SimpleAmount(5)))),
    WonderStage(Cost(0, MultiSet(Stone, Stone, Stone, Stone)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val GIZAH_B = Civilization("GIZAH_B", Stone, List(
    WonderStage(Cost(0, MultiSet(Wood, Wood)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Stone, Stone, Stone)), Set(VictoryPointSymbol(SimpleAmount(5)))),
    WonderStage(Cost(0, MultiSet(Clay, Clay, Clay)), Set(VictoryPointSymbol(SimpleAmount(5)))),
    WonderStage(Cost(0, MultiSet(Tapestry, Stone, Stone, Stone, Stone)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val EPHESOS_A = Civilization("EPHESOS_A", Paper, List(
    WonderStage(Cost(0, MultiSet(Stone, Stone)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Wood, Wood)), Set(CoinSymbol(SimpleAmount(9)))),
    WonderStage(Cost(0, MultiSet(Paper, Paper)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val EPHESOS_B = Civilization("EPHESOS_B", Paper, List(
    WonderStage(Cost(0, MultiSet(Stone, Stone)), Set(VictoryPointSymbol(SimpleAmount(2)))),
    WonderStage(Cost(0, MultiSet(Wood, Wood)), Set(VictoryPointSymbol(SimpleAmount(3)), CoinSymbol(SimpleAmount(4)))),
    WonderStage(Cost(0, MultiSet(Paper, Tapestry, Glass)), Set(VictoryPointSymbol(SimpleAmount(5)), CoinSymbol(SimpleAmount(4))))
  ))
  val BABYLON_A = Civilization("BABYLON_A", Clay, List(
    WonderStage(Cost(0, MultiSet(Clay, Clay)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Wood, Wood, Wood)), Set(Tablet | Compass | Gear)),
    WonderStage(Cost(0, MultiSet(Clay, Clay, Clay)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val BABYLON_B = Civilization("BABYLON_B", Clay, List(
    WonderStage(Cost(0, MultiSet(Tapestry, Clay)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Glass, Wood, Wood)), Set(PlayLastCardEachAge)),
    WonderStage(Cost(0, MultiSet(Paper, Clay, Clay, Clay)), Set(Tablet | Compass | Gear))
  ))
  val PETRA_A = Civilization("PETRA_A", Clay, List(
    WonderStage(Cost(0, MultiSet(Wood, Stone)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(new Cost(7), Set(VictoryPointSymbol(SimpleAmount(7)))),
    WonderStage(Cost(0, MultiSet(Paper, Wood, Stone, Stone)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val PETRA_B = Civilization("PETRA_B", Clay, List(
    WonderStage(Cost(0, MultiSet(Clay, Clay, Ore, Ore)), Set(VictoryPointSymbol(SimpleAmount(3)), PayBankSymbol(SimpleAmount(2)))),
    WonderStage(new Cost(14), Set(VictoryPointSymbol(SimpleAmount(14))))
  ))
  val BYZANTIUM_A = Civilization("BYZANTIUM_A", Stone, List(
    WonderStage(Cost(0, MultiSet(Ore, Clay)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Paper, Wood, Wood)), Set(VictoryPointSymbol(SimpleAmount(2)), DiplomacySymbol)),
    WonderStage(Cost(0, MultiSet(Glass, Tapestry, Clay, Clay)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val BYZANTIUM_B = Civilization("BYZANTIUM_B", Stone, List(
    WonderStage(Cost(0, MultiSet(Glass, Paper, Ore, Wood)), Set(VictoryPointSymbol(SimpleAmount(3)), DiplomacySymbol)),
    WonderStage(Cost(0, MultiSet(Ore, Ore, Clay, Tapestry)), Set(VictoryPointSymbol(SimpleAmount(4)), DiplomacySymbol))
  ))

  val baseCivilizations = Set(
    (RHODOS_A, RHODOS_B),
    (ALEXANDRIA_A, ALEXANDRIA_B),
    (HALIKARNASSOS_A, HALIKARNASSOS_B),
    (OLYMPIA_A, OLYMPIA_B),
    (GIZAH_A, GIZAH_B),
    (EPHESOS_A, EPHESOS_B),
    (BABYLON_A, BABYLON_B)
  )
  val cityCivilizations = Set(
    (BYZANTIUM_A, BYZANTIUM_B),
    (PETRA_A, PETRA_B)
  )

}
