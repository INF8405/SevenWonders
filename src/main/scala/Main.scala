package com.github.jedesah


object SevenWonders 
{
  def beginGame( nbPlayers: Int ): Game = ???

  class Card( 
    name: String, 
    cost: Map[Resource, Int]
  )

  class EvolutionCard
  {
    name: String, 
    cost: Map[Resource, Int],
    evolutions: Set[Card] 
  } extends Card( name, cost )

  sealed trait ScienceCategory
  object ScienceCompass extends ScienceCategory
  object ScienceGear extends ScienceCategory
  object ScienceStone extends ScienceCategory

  case class ScienceCard(
    name: String, 
    cost: Map[Resource, Int], 
    evolutions: Set[Card], 
    category: ScienceCategory
  ) extends EvolutionCard( name, cost, evolutions )

  case class MilitaryCard(
    name: String, 
    cost: Map[Resource, Int],
    evolutions: Set[Card], 
    value: Int
  ) extends EvolutionCard( name, cost, evolutions )

  class CommercialCard(
    name: String,
    cost: Map[Resource, Int], 
    evolutions: Set[Card]
  ) extends EvolutionCard( name, cost, evolutions )

  case class RebateCommercialCard(
    name: String,
    cost: Map[Resource, Int],
    evolutions: Set[Card], 
    affectedResources: List[Resource],
    fromWho: List[NeighboorReference]
  ) extends CommercialCard( name, cost, evolutions )

  case class ProductionCommercialCard(
    name: String, 
    cost: Map[Resource, Int], 
    evolutions: Set[Card], 
    prod: Production
  ) extends CommercialCard( name, cost, evolutions )

  case class RewardCommercialCard(
    name: String, 
    cost: Map[Resource, Int], 
    evolutions: List[Card], 
    coinReward: Option[CoinReward], 
    victoryReward: Option[VictoryPointReward]
  ) extends CommercialCard( name, cost, evolutions )

  class ResourceCard(
    name: String, 
    cost: Map[Resource, Int], 
    production: Production
  ) extends Card(name, cost )

  case class RawMaterialCard(
    name: String, 
    cost: Map[Resource, Int],
    production: Production
  ) extends ResourceCard(name, cost, production)

  case class ManufacturedGoodCard(
    name: String,
    cost: Map[Resource, Int], 
    production: Production
  ) extends ResourceCard(name, cost, production)
  
  case class CivilianStructureCard(
    name: String,
    cost: Map[Resource, Int],
    evolutions: Set[Card],
    amount: Int
  ) extends EvolutionCard( name, cost, evolutions )

  case class GuildCard(
    name: String,
    cost: Map[Resource, Int]//, 
    //vicPointReward: VictoryPointReward
  ) extends Card( name, cost )

  case class CoinReward(
    amount: Int, 
    forEach: ClassTag[Card], 
    from: Set[PlayerReference]
  )

  case class VictoryPointReward(
    amount: Int//, 
    //forEach: ClassTag[Card], 
    //from: Set[PlayerReference]
  )

  sealed trait Resource
  object RawMaterial extends Resource
  object ManufacturedGood extends Resource
  object Clay extends RawMaterial
  object Wood extends RawMaterial
  object Ore extends RawMaterial
  object Stone extends RawMaterial
  object Glass extends ManufacturedGood
  object Paper extends ManufacturedGood
  object Tapestry extends ManufacturedGood

  sealed trait PlayerReference
  object Left extends PlayerReference
  object Right extends PlayerReference
  object Self extends PlayerReference

  trait Production {
    def consume(resources: Map[Resource, Int]): List[Map[Resource, Int]]
    def +(other: Production): Production
  }
  case class OptionalProduction(possibilities: List[CumulativeProdruction]) extends Production {
    def consume(resources: Map[Resource, Int]) = ???
    def +(other: Production) = ???
    override def equals(other: Any) = ???
  }
  case class CumulativeProdruction(resources: List[Resource]) extends Production {
    def consume(resources: Map[Resource, Int]) = ???
    def +(other: Production) = ???
    override def equals(other: Any) = ???
  }

  case class Player(hand: List[Card], coins: Int, battleMarkers: List[BattleMarker], played: List[Card]) {
    def discard(card: Card): Player = ???
    def play(card: Card, consume: Map[Resource, Set[PlayerReference]]): Player = ???
    def playableCards(availableThroughTrade: List[Production]): List[Card] = ???
    def totalPossibleProductions: List[Production] = ???
    def tradableProductions: List[Production] = ???
    def score: Int = ???
  }

  type Age = Int

  case class Game(players: List[Player], cards: Map[Age, List[Card]], discarded: List[Card]) {
    def getNeighboors(player: Player): List[Player] = ???
    def playTurn(actions: Map[Player, Action]): Game = ???
    def currentAge = cards.keys.toList.reverse.find(cards(_) == Nil).get
  }

  class Action(card: Card)
  case class PlayAction(card: Card) extends Action(card)
  case class DiscardAction(card: Card) extends Action(card)

  sealed trait BattleMarker
  class LostBattleMarker extends BattleMarker
  case class WonBattleMarker(vicPoints: Int) extends BattleMarker

  type PlayerAmount = Int

  case class GameSetup(allCards: Map[Age, Map[PlayerAmount, List[Card]]], guildCards: List[GuildCard]) {
    def generateCards: Map[Age, List[Card]] = ???
  }
}