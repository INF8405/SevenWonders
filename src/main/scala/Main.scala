package com.github.jedesah


object SevenWonders {
  def beginGame(nbPlayers: Int): Game = ???

  class Card(name: String, cost: Map[Resource, Int], evolutions: List[Card])

  trait ScienceCategory
  trait Category1 extends ScienceCategory
  trait Categpry2 extends ScienceCategory
  trait Category3 extends ScienceCategory

  case class ScienceCard(name: String, cost: Map[Resource, Int], evolutions: List[Card], category: ScienceCategory) extends Card(name, cost, evolutions)
  case class MilitaryCard(name: String, cost: Map[Resource, Int], evolutions: List[Card], value: Int) extends Card(name, cost, evolutions)
  class CommerceCard(name: String, cost: Map[Resource, Int], evolutions: List[Card]) extends Card(name, cost, evolutions)
  case class RebateCommerceCard(name: String, cost: Map[Resource, Int], evolutions: List[Card], affectedResources: List[Resource], fromWho: List[NeighboorReference]) extends CommerceCard(name, cost, evolutions)
  case class RewardCommerceCard(name: String, cost: Map[Resource, Int], evolutions: List[Card], coinReward: Option[CoinReward], victoryReward: Option[VictoryPointReward]) extends CommerceCard(name, cost, evolutions)
  case class ProductionCommerceCard(name: String, cost: Map[Resource, Int], evolutions: List[Card], prod: Production) extends CommerceCard(name, cost, evolutions)
  class ResourceCard(name: String, cost: Map[Resource, Int], evolutions: List[Card], production: Production) extends CommerceCard(name, cost, evolutions)
  case class BasicResourceCard(name: String, cost: Map[Resource, Int], evolutions: List[Card], production: Production) extends ResourceCard(name, cost, evolutions, production)
  case class AdvancedResourceCard(name: String, cost: Map[Resource, Int], evolutions: List[Card], production: Production) extends ResourceCard(name, cost, evolutions, production)
  case class PrestigeCard(name: String, cost: Map[Resource, Int], evolutions: List[Card], amount: Int) extends Card(name, cost, evolutions)
  case class GuildCard(name: String, cost: Map[Resource, Int], evolutions: List[Card], vicPointReward: VictoryPointReward) extends Card(name, cost, evolutions)

  import scala.reflect.ClassTag

  case class CoinReward(amount: Int, forEach: ClassTag[Card], from: List[PlayerReference])
  case class VictoryPointReward(amount: Int, forEach: ClassTag[Card], from: List[PlayerReference])

  trait Resource
  trait BasicResource extends Resource
  trait AdvancedResource extends Resource
  trait Clay extends BasicResource
  trait Wood extends BasicResource
  trait Ore extends BasicResource
  trait Stone extends BasicResource
  trait Glass extends AdvancedResource
  trait Paper extends AdvancedResource
  trait Tapestry extends AdvancedResource

  trait PlayerReference
  trait NeighboorReference
  trait Left extends NeighboorReference
  trait Right extends NeighboorReference
  trait Self extends PlayerReference

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
    def play(card: Card, trades: Map[Resource, List[PlayerReference]]): Player = ???
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

  trait BattleMarker
  trait LostBattleMarker extends BattleMarker
  case class WonBattleMarker(vicPoints: Int) extends BattleMarker

  type PlayerAmount = Int

  case class GameSetup(allCards: Map[Age, Map[PlayerAmount, List[Card]]], guildCards: List[GuildCard]) {
    def generateCards: Map[Age, List[Card]] = ???
  }
}