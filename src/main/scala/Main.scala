package com.github.jedesah

import com.sidewayscoding.Multiset

object SevenWonders 
{
  def beginGame( nbPlayers: Int ): Game = ???

  class Card( 
    name: String, 
    cost: Multiset[Resource],
    evolutions: Set[Card]
  )

  sealed trait ScienceCategory
  object ScienceCompass extends ScienceCategory
  object ScienceGear extends ScienceCategory
  object ScienceStone extends ScienceCategory

  case class ScienceCard(
    name: String,
    cost: Multiset[Resource],
    evolutions: Set[Card],
    category: ScienceCategory
  ) extends Card( name, cost, evolutions )

  case class MilitaryCard(
    name: String,
    cost: Multiset[Resource],
    evolutions: Set[Card],
    value: Int
  ) extends Card( name, cost, evolutions )

  class CommercialCard(
    name: String,
    cost: Multiset[Resource],
    evolutions: Set[Card]
  ) extends Card( name, cost, evolutions )

  case class RebateCommercialCard(
    name: String,
    cost: Multiset[Resource],
    evolutions: Set[Card],
    affectedResources: Set[Resource],
    fromWho: Set[NeighboorReference]
  ) extends CommercialCard( name, cost, evolutions )

  case class ProductionCommercialCard(
    name: String,
    cost: Multiset[Resource],
    evolutions: Set[Card],
    prod: Production
  ) extends CommercialCard( name, cost, evolutions )

  case class RewardCommercialCard(
    name: String, 
    cost: Multiset[Resource],
    evolutions: Set[Card],
    coinReward: Option[CoinReward],
    victoryReward: Option[VictoryPointReward]
  ) extends CommercialCard( name, cost, evolutions )

  class ResourceCard(
    name: String,
    cost: Multiset[Resource],
    production: Production
  ) extends Card(name, cost, Set() )

  case class RawMaterialCard(
    name: String,
    cost: Multiset[Resource],
    production: Production
  ) extends ResourceCard(name, cost, production)

  case class ManufacturedGoodCard(
    name: String,
    cost: Multiset[Resource],
    production: Production
  ) extends ResourceCard(name, cost, production)
  
  case class CivilianStructureCard(
    name: String,
    cost: Multiset[Resource],
    evolutions: Set[Card],
    amount: Int
  ) extends Card( name, cost, evolutions )

  case class GuildCard(
    name: String,
    cost: Multiset[Resource],
    vicPointReward: VictoryPointReward
  ) extends Card( name, cost, Set() )

  case class CoinReward(
    amount: Int,
    //forEach: Manifest[Card],
    from: Set[PlayerReference]
  )

  case class VictoryPointReward(
    amount: Int,
    //forEach: ClassTag[Card],
    from: Set[PlayerReference]
  )

  sealed trait Resource
  sealed trait RawMaterial extends Resource
  sealed trait ManufacturedGood extends Resource
  object Clay extends RawMaterial
  object Wood extends RawMaterial
  object Ore extends RawMaterial
  object Stone extends RawMaterial
  object Glass extends ManufacturedGood
  object Paper extends ManufacturedGood
  object Tapestry extends ManufacturedGood

  sealed trait PlayerReference
  sealed trait NeighboorReference extends PlayerReference
  object Left extends NeighboorReference
  object Right extends NeighboorReference
  object Self extends PlayerReference

  trait Production {
    def consume(resources: Multiset[Resource]): Set[Multiset[Resource]]
    def +(other: Production): Production
  }
  case class OptionalProduction(possibilities: Set[CumulativeProduction]) extends Production {
    def consume(resources: Multiset[Resource]): Set[Multiset[Resource]] = ???
    def +(other: Production): Production = ???
  }
  case class CumulativeProduction(resources: Multiset[Resource]) extends Production {
    def this(resource: Resource) = this(Multiset(resource))
    def consume(resources: Multiset[Resource]): Set[Multiset[Resource]] = ???
    def +(other: Production): Production = ???
  }

  case class Player(hand: Set[Card], coins: Int, battleMarkers: Multiset[BattleMarker], played: Set[Card]) {
    def discard(card: Card): Player = ???
    def play(card: Card, consume: Map[Resource, Multiset[NeighboorReference]]): Player = ???
    def playableCards(availableThroughTrade: Set[Production]): Set[Card] = ???
    def totalPossibleProductions: Set[Production] = ???
    def tradableProductions: Set[Production] = ???
    def score: Int = ???
  }

  type Age = Int

  case class Game(players: List[Player], cards: Map[Age, Set[Card]], discarded: Set[Card]) {
    def getNeighboors(player: Player): Set[Player] = ???
    def getLeftNeighboor(player: Player): Player = ???
    def getRightNeighboor(player: Player): Player = ???
    def playTurn(actions: Map[Player, Action]): Game = ???
    def currentAge = cards.keys.toList.reverse.find(cards(_) == Nil).get
  }

  class Action(card: Card)
  case class PlayAction(card: Card, consume: Map[Resource, Multiset[NeighboorReference]]) extends Action(card)
  case class DiscardAction(card: Card) extends Action(card)

  sealed trait BattleMarker
  class DefeatBattleMarker extends BattleMarker
  case class VicotryBattleMarker(vicPoints: Int) extends BattleMarker

  type PlayerAmount = Int

  case class GameSetup(allCards: Map[Age, Map[PlayerAmount, Multiset[Card]]], guildCards: Set[GuildCard]) {
    def generateCards: Map[Age, Multiset[Card]] = ???
  }
}