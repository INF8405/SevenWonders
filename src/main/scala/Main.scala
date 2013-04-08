package com.github.jedesah

import util.Random
import utils.{Math, Utils}
import Utils._
import collection.MultiMap
import collection.MultiSet
import collection.Circle
import collection.conversions._

object SevenWonders 
{

  def beginGame( nbPlayers: Int ): Game = {
    val cards = classicSevenWonders.generateCards(nbPlayers)
    val chosenCivilizations = Random.shuffle(civilizations.toList).take(nbPlayers)
    val players = chosenCivilizations.map{
      civ =>
        Player(civ, MultiSet(), 3)
    }
    Game(new Circle[Player](players: _*), cards, MultiSet()).beginAge()
  }

  case class Cost(coins: Int, resources: MultiSet[Resource]) {
    def this(resources: MultiSet[Resource]) = this(0, resources)
    def this(coins: Int) = this(coins, MultiSet())
  }
  object Free extends Cost(0)

  sealed trait Symbol {
    /**
     * Implements the immediate effect of this symbol on the game.
     * Default implementation: the symbol has no immediate effect.
     * Most symbols should have no immediate effect like a production symbol, military symbol, etc.
     * But commerce reward coins and maybe one day city cards have an immediate effect on the game
     * which should be implemented in this method that will be called after a playable has been played
     * @param current The current state of the game
     * @param playedBy The player who played a playable with this symbol on it
     * @return The new game after resolving the symbol
     */
    def resolve(current:Game, playedBy: Player): Game = current
  }

  trait ScienceSymbol extends Symbol {
    def +(other: ScienceSymbol): ScienceSymbol = other match {
      case other: SimpleScienceSymbol => this + other
      case other: OptionalScienceSymbol => this + other
    }
    def +(other: SimpleScienceSymbol): ScienceSymbol
    def +(other: OptionalScienceSymbol): OptionalScienceSymbol
    def |(other: ScienceSymbol): ScienceSymbol = other match {
      case other: SimpleScienceSymbol => this | other
      case other: OptionalScienceSymbol => this | other
    }
    def |(other: SimpleScienceSymbol): ScienceSymbol
    def |(other: OptionalScienceSymbol): OptionalScienceSymbol
    def victoryPointValue: Int
  }
  case class SimpleScienceSymbol(compass: Int, gear: Int, tablet: Int) extends ScienceSymbol {
    def +(other: SimpleScienceSymbol) = SimpleScienceSymbol(compass + other.compass, gear + other.gear, tablet + other.tablet)
    def +(other: OptionalScienceSymbol) = other + this
    def |(other: SimpleScienceSymbol) =
      if (other != this)
        OptionalScienceSymbol(Set(this, other))
      else
        this
    def |(other: OptionalScienceSymbol) = other | this
    def victoryPointValue = {
      val setValue = List(compass, gear, tablet).min * 7
      val stackValue = List(compass, gear, tablet).map(Math.pow(_, 2)).sum
      setValue + stackValue
    }
  }
  case class OptionalScienceSymbol(alternatives: Set[ScienceSymbol]) extends ScienceSymbol {
    def +(other: SimpleScienceSymbol) = OptionalScienceSymbol(alternatives.map(_ + other))
    def +(other: OptionalScienceSymbol) = {
      val newAlternatives = for {
        alt1 <- alternatives;
        alt2 <- other.alternatives
      } yield alt1 + alt2
      OptionalScienceSymbol(newAlternatives.toSet)
    }
    override def |(other: ScienceSymbol): OptionalScienceSymbol = other match {
      case other: SimpleScienceSymbol => this | other
      case other: OptionalScienceSymbol => this | other
    }
    def |(other: SimpleScienceSymbol): OptionalScienceSymbol = OptionalScienceSymbol(alternatives + other)
    def |(other: OptionalScienceSymbol) =
      alternatives.foldLeft[OptionalScienceSymbol](other){(other, alternative) => other | alternative}
    def victoryPointValue = alternatives.map(_.victoryPointValue).max
  }

  trait Production extends Symbol {
    def consume(resources: MultiSet[Resource]): Set[MultiSet[Resource]]
    def consumes(resource: Resource): Boolean
    def canProduce: Set[Resource]
    def cannotProduce: Set[Resource] = allResources -- canProduce
    def -(resource: Resource): Production
    def +(other: Production): Production = other match {
      case other: OptionalProduction => this + other
      case other: CumulativeProduction => this + other
    }
    def +(other: CumulativeProduction): Production
    def +(other: OptionalProduction): OptionalProduction
    def |(other: Production): Production = other match {
      case other: OptionalProduction => this | other
      case other: CumulativeProduction => this | other
    }
    def |(other: CumulativeProduction): Production
    def |(other: OptionalProduction): OptionalProduction
  }

  case class OptionalProduction(possibilities: Set[CumulativeProduction]) extends Production {
    def consume(resources: MultiSet[Resource]): Set[MultiSet[Resource]] = possibilities.map(_.consume(resources).head)
    def consumes(resource: Resource) = possibilities.exists(_.consumes(resource))
    def canProduce = possibilities.map(_.canProduce).reduce(_ ++ _)
    def -(resource: Resource) = OptionalProduction(possibilities.map(_ - resource))
    def +(other: OptionalProduction): OptionalProduction =
      OptionalProduction(possibilities.map( poss1 => other.possibilities.map( poss2 => poss1 + poss2)).flatten)
    def +(other: CumulativeProduction): OptionalProduction = OptionalProduction(possibilities.map(_ + other))
    def |(other: OptionalProduction): OptionalProduction =
      OptionalProduction(possibilities ++ other.possibilities)
    def |(other: CumulativeProduction): OptionalProduction = OptionalProduction(possibilities + other)
  }
  case class CumulativeProduction(produces: MultiSet[Resource]) extends Production {
    def this(resource: Resource) = this(MultiSet(resource))
    def consume(resources: MultiSet[Resource]): Set[MultiSet[Resource]] = Set(resources -- produces)
    def consumes(resource: Resource) = produces.contains(resource)
    def canProduce = produces.toSet
    def -(resource: Resource) = CumulativeProduction(produces - resource)
    def +(other: OptionalProduction) = other + this
    def +(other: CumulativeProduction) = CumulativeProduction(produces ++ other.produces)
    def |(other: OptionalProduction) = other | this
    def |(other: CumulativeProduction) = OptionalProduction(Set(this, other))
  }
  val NoProduction = CumulativeProduction(MultiSet[Resource]())

  case class MilitarySymbol(strength: Int) extends Symbol
  case class VictoryPointSymbol(reward: Amount) extends Symbol
  case class CoinSymbol(reward: Amount) extends Symbol {
    override def resolve(current:Game, playedBy: Player): Game = {
      reward match {
        case ThreeWayAmount(left, self, right) => {
          val oldLeft = current.players.getLeft(playedBy)
          val newLeft = oldLeft.addCoins(left)
          val oldRight = current.players.getRight(playedBy)
          val newRight = oldRight.addCoins(right)
          val newSelf = playedBy.addCoins(self)
          current.copy(players = current.players.replace(oldLeft, newLeft).replace(oldRight, newRight).replace(playedBy, newSelf))
        }
        case _ => {
          val coinsToAdd = playedBy.calculateAmount(reward, current.getNeighboorsStuff(playedBy))
          current.copy(players = current.players.replace(playedBy, playedBy.addCoins(coinsToAdd)))
        }
      }
    }
  }
  case class RebateSymbol(affectedResources: Set[Resource], fromWho: Set[NeighborReference], multiplicity: Int = Int.MaxValue) extends Symbol
  object FreeBuildEachAge extends Symbol
  object GrabFromDiscardPile extends Symbol
  object CopyGuildCard extends Symbol
  object PlayLastCardEachAge extends Symbol
  object BuildWondersForFree extends Symbol
  object ProduceResourceAlreadyProduced extends Symbol
  object ProduceResourceNotProduced extends Symbol
  object DiplomacySymbol extends Symbol {
    override def resolve(current:Game, playedBy: Player): Game = {
      current.copy(players = current.players.replace(playedBy, playedBy.copy(stuff = playedBy.stuff + new DiplomacyToken)))
    }
  }
  class StealScience extends Symbol
  case class PayBankSymbol(amount: Amount) extends Symbol {
    override def resolve(current:Game, playedBy: Player): Game = {
      val newPlayers = current.players.map[Player] {
        player =>
          if (player == playedBy) player
          else player.removeCoins(player.calculateAmount(amount, current.getNeighboorsStuff(player)))
      }
      current.copy(players = newPlayers)
    }
  }

  trait GameElement

  trait PlayableElement extends GameElement {
    val cost: Cost
    val symbols: Set[Symbol]
  }

  class Card( 
    val name: String,
    val cost: Cost,
    val evolutions: Set[Card],
    val symbols:  Set[Symbol]
  ) extends PlayableElement

  case class WonderStage(cost: Cost, symbols: Set[Symbol]) extends PlayableElement

  val compass = SimpleScienceSymbol(1, 0, 0)
  val gear = SimpleScienceSymbol(0, 1, 0)
  val tablet = SimpleScienceSymbol(0, 0, 1)

  case class ScienceCard(
    override val name: String,
    override val cost: Cost,
    override val evolutions: Set[Card],
    scienceSymbol: ScienceSymbol
  ) extends Card( name, cost, evolutions, Set(scienceSymbol) )

  case class MilitaryCard(
    override val name: String,
    override val cost: Cost,
    override val evolutions: Set[Card],
    strength: Int
  ) extends Card( name, cost, evolutions, Set(MilitarySymbol(strength)))

  case class CommercialCard(
    override val name: String,
    override val cost: Cost,
    override val evolutions: Set[Card],
    override val symbols: Set[Symbol]
  ) extends Card( name, cost, evolutions, symbols )

  class ResourceCard(
    name: String,
    cost: Cost,
    val prod: Production
  ) extends Card(name, cost, Set(), Set(prod) )

  case class RawMaterialCard(
    override val name: String,
    override val cost: Cost,
    production: Production
  ) extends ResourceCard(name, cost, production)

  case class ManufacturedGoodCard(
    override val name: String,
    override val cost: Cost,
    production: Production
  ) extends ResourceCard(name, cost, production) {
    def this(name: String, production: Production) = this(name, Cost(0, MultiSet()), production)
  }
  
  case class CivilianCard(
    override val name: String,
    override val cost: Cost,
    override val evolutions: Set[Card],
    value: Int
  ) extends Card(name, cost, evolutions, Set(VictoryPointSymbol(SimpleAmount(value))))

  case class GuildCard(override val name: String, override val cost: Cost, override val symbols: Set[Symbol]) extends Card(name, cost, Set(), symbols)

  case class CityCard(override val name: String, override val cost: Cost,override val symbols: Set[Symbol]) extends Card(name, cost, Set(), symbols)

  trait Amount
  case class SimpleAmount(value: Int) extends Amount
  case class VariableAmount(
    amount: Int,
    forEach: Class[_ <: GameElement],
    from: Set[PlayerReference]
  ) extends Amount
  case class ThreeWayAmount(left: Int, self: Int, right: Int) extends Amount

  sealed trait Resource
  sealed trait RawMaterial extends Resource
  sealed trait ManufacturedGood extends Resource
  object Clay extends RawMaterial {
    override def toString = "Clay"
  }
  object Wood extends RawMaterial {
    override def toString = "Wood"
  }
  object Ore extends RawMaterial {
    override def toString = "Ore"
  }
  object Stone extends RawMaterial {
    override def toString = "Stone"
  }
  object Glass extends ManufacturedGood {
    override def toString = "Glass"
  }
  object Paper extends ManufacturedGood {
    override def toString = "Paper"
  }
  object Tapestry extends ManufacturedGood {
    override def toString = "Tapestry"
  }

  val rawMaterials = Set(Clay, Wood, Ore, Stone)
  val manufacturedGoods = Set(Glass, Paper, Tapestry)
  val allResources = rawMaterials ++ manufacturedGoods

  sealed trait PlayerReference
  sealed trait NeighborReference extends PlayerReference
  object Left extends NeighborReference
  object Right extends NeighborReference
  object Self extends PlayerReference


  implicit def ResourceToProduction(value: Resource) = new CumulativeProduction(value)

  type Trade = MultiMap[Resource, NeighborReference]

  case class Player(civilization: Civilization,
                    hand: MultiSet[Card] = MultiSet(),
                    coins: Int = 0,
                    stuff: MultiSet[GameElement] = MultiSet(),
                    played: Set[Card] = Set(),
                    nbWonders: Int = 0,
                    hasBuiltForFreeThisAge: Boolean = false
                    ) {
    def discard(card: Card): Player = this.copy(hand = hand - card, coins = coins + 3)

    /**
     * Handles all state changing relative to this player when he plays a card.
     * @param card The card to build
     * @param trade The trade used to build this card. Can be an empty trade
     * @return The updated Player state along with the amount of coins given to the left and right players
     */
    def build(card: Card, trade: Trade): (Player, (Int, Int)) = {
      val player = this.copy(hand = hand - card,coins = coins - cost(trade).sum - card.cost.coins, played = played + card)
      (player, cost(trade))
    }

    def buildWonderStage(card: Card, trade: Trade): (Player, (Int, Int)) = {
      val totalCost = if (allSymbols.contains(BuildWondersForFree)) 0 else cost(trade).sum + civilization.stagesOfWonder(nbWonders).cost.coins
      val player = this.copy(hand = hand - card, coins = coins - totalCost, nbWonders = nbWonders + 1)
      (player, cost(trade))
    }

    def canBuildWonderStage(availableThroughTrade: Map[NeighborReference, Production]): Boolean =
      if (nbWonders == civilization.stagesOfWonder.size) false
      else if (allSymbols.contains(BuildWondersForFree)) true
      else canBuild(civilization.stagesOfWonder(nbWonders), availableThroughTrade)

    def buildForFree(card: Card): Player = {
      if (!canBuildForFree) throw new UnsupportedOperationException("It is not possible for this player to use this action from his current state")
      else this.copy(hand = hand - card, played = played + card, hasBuiltForFreeThisAge = true)
    }

    def canBuildForFree = allSymbols.contains(FreeBuildEachAge) && !hasBuiltForFreeThisAge

    def playableCards(availableThroughTrade: Map[NeighborReference, Production]): Set[Card] =
      if (canBuildForFree)
        hand.toSet
      else
        hand.toSet.filter( card => canBuild(card, availableThroughTrade))

    def totalProduction: Production = {
      val productionSymbols = allSymbols.filter(_.isInstanceOf[Production]).map(_.asInstanceOf[Production])
      val normalProduction = productionSymbols.foldLeft(civilization.base)(_ + _)
      val extraCanProduce: Production =
        if (allSymbols.contains(ProduceResourceAlreadyProduced))
          OptionalProduction(tradableProduction.canProduce.map(new CumulativeProduction(_)))
        else
          NoProduction
      val extraCannotProduce: Production =
        if (allSymbols.contains(ProduceResourceNotProduced))
          OptionalProduction(tradableProduction.cannotProduce.map(new CumulativeProduction(_)))
        else
          NoProduction
      normalProduction + extraCanProduce + extraCannotProduce
    }

    def tradableProduction: Production = {
      val productionCards = played.filter(_.isInstanceOf[ResourceCard]).map(_.asInstanceOf[ResourceCard])
      productionCards.foldLeft(civilization.base)(_ + _.prod)
    }

    def addCoins(toAdd: Int): Player = this.copy(coins = coins + toAdd)

    def removeCoins(toRemove: Int): Player =
      if (coins > toRemove) this.copy(coins = coins - toRemove)
      else {
        val debtValue = toRemove - coins
        this.copy(coins = 0, stuff = stuff + DebtToken(debtValue))
      }

    def wonderStagesBuilt: List[WonderStage] = civilization.stagesOfWonder.take(nbWonders)

    def allSymbols: MultiSet[Symbol] = allPlayables.map(_.symbols).flatten

    def allPlayables: MultiSet[PlayableElement] = played.toMultiSet ++ wonderStagesBuilt

    def allGameElements: MultiSet[GameElement] = allPlayables ++ stuff

    def battleMarkers: MultiSet[BattleMarker] = stuff.filter(_.isInstanceOf[BattleMarker]).map(_.asInstanceOf[BattleMarker])

    def debtTokens: MultiSet[DebtToken] = stuff.filter(_.isInstanceOf[DebtToken]).map(_.asInstanceOf[DebtToken])

    def hasDiplomacy: Boolean = !stuff.filter(_.isInstanceOf[DiplomacyToken]).isEmpty

    def removeDiplomacyToken: Player =
      if (hasDiplomacy) this.copy(stuff = stuff - new DiplomacyToken)
      else this

    def militaryStrength: Int = allSymbols.filter(_.isInstanceOf[MilitarySymbol]).map(_.asInstanceOf[MilitarySymbol]).map(_.strength).sum

    def score(neighborStuff: Map[NeighborReference, MultiSet[GameElement]]): Int =
      scienceScore(neighborStuff) + militaryScore + civilianScore + commerceScore + guildScore(neighborStuff) + wondersScore(neighborStuff) + coinScore

    def coinScore = coins/3 - debtScorePenalty

    def debtScorePenalty = debtTokens.map(_.amount).sum

    def scienceScore(neighborStuff: Map[NeighborReference, MultiSet[GameElement]] = Map()): Int = {
      val (pointsFromCopyGuildCard, usedScienceGuildCard) = copyGuildCardBonus(neighborStuff)
      scienceValue(neighborStuff).victoryPointValue + (if (usedScienceGuildCard) pointsFromCopyGuildCard else 0)
    }

    def scienceValue(neighborStuff: Map[NeighborReference, MultiSet[GameElement]] = Map()) = {
      def steal(stealSymbols: MultiSet[StealScience], neighborScienceSymbols: MultiSet[ScienceSymbol]): ScienceSymbol = {
        if(stealSymbols.isEmpty) SimpleScienceSymbol(0,0,0)
        else {
          OptionalScienceSymbol(neighborScienceSymbols.toSet.map(symbol => symbol + steal(stealSymbols.tail, neighborScienceSymbols - symbol)))
        }
      }
      val scienceSymbols = allSymbols.filter(_.isInstanceOf[ScienceSymbol]).map(_.asInstanceOf[ScienceSymbol])
      val stealSymbols = allSymbols.filter(_.isInstanceOf[StealScience]).map(_.asInstanceOf[StealScience])
      val neighborScienceSymbolsFromScienceCards = neighborStuff.values.reduceOption(_ ++ _).getOrElse(MultiSet()).filter(_.isInstanceOf[ScienceCard]).map(_.asInstanceOf[ScienceCard].scienceSymbol)
      scienceSymbols.foldLeft[ScienceSymbol](steal(stealSymbols, neighborScienceSymbolsFromScienceCards))(_ + _)

    }

    def militaryScore = battleMarkers.map(_.vicPoints).sum

    def civilianScore = {
      val civilianCards = played.filter(_.isInstanceOf[CivilianCard]).map(_.asInstanceOf[CivilianCard])
      // n.b. We need to convert the set to a multiset or else we would lose some information as some
      // cards can have the same victory point value
      civilianCards.toMultiSet.map(_.value).sum
    }

    def commerceScore: Int = {
      val commerceCards = played.filter(_.isInstanceOf[CommercialCard]).map(_.asInstanceOf[CommercialCard])
      calculateVictoryPoints(commerceCards.toMultiSet.asInstanceOf[MultiSet[PlayableElement]], Map())
    }

    def guildScore(neighborStuff: Map[NeighborReference, MultiSet[GameElement]]): Int = {
      val guildCards = played.filter(_.isInstanceOf[GuildCard]).map(_.asInstanceOf[GuildCard])
      if (guildCards.isEmpty) 0
      else {
        guildCards.map(card => calculateVictoryPoints(MultiSet(card), neighborStuff)).sum
      }
    }

    def wondersScore(neighborStuff: Map[NeighborReference, MultiSet[GameElement]]): Int =
      if (wonderStagesBuilt.isEmpty) 0
      else {
        val standardPoints = calculateVictoryPoints(wonderStagesBuilt.asInstanceOf[MultiSet[PlayableElement]], neighborStuff)
        val (pointsFromCopyGuildCard, usedScienceGuildCard) = copyGuildCardBonus(neighborStuff)
        standardPoints + (if (usedScienceGuildCard) 0 else pointsFromCopyGuildCard)
      }

    type UsedScienceGuildCard = Boolean

  /**
   * Computes the bonus amount of points awarded by a copy guild card from a neighboor
   * @param neighborStuff
   * @return The amount of points that the best guild card for this player would give. It also returns if the ScienceGuildCard is the best one to copy.
   */
    def copyGuildCardBonus(neighborStuff: Map[NeighborReference, MultiSet[GameElement]]): (Int, UsedScienceGuildCard) =
      if (!wonderStagesBuilt.map(_.symbols).flatten.contains(CopyGuildCard))
        (0, false)
      else {
        val neigborGuildCards = neighborStuff.values.reduce(_ ++ _).filter(_.isInstanceOf[GuildCard]).map(_.asInstanceOf[GuildCard])
        if (neigborGuildCards.isEmpty)
          (0, false)
        else {
          val scienceBonus =
            if (neigborGuildCards.contains(SCIENTISTS_GUILD))
              (scienceValue(neighborStuff) + SCIENTISTS_GUILD.symbols.head.asInstanceOf[ScienceSymbol]).victoryPointValue - scienceValue(neighborStuff).victoryPointValue
            else
              0
          val otherBonus = neigborGuildCards.map(guildCard => calculateVictoryPoints(MultiSet(guildCard), neighborStuff)).max
          if (scienceBonus > otherBonus) (scienceBonus, true) else (otherBonus, false)
        }
      }

    def calculateVictoryPoints(of: MultiSet[PlayableElement], neighborCards: Map[NeighborReference, MultiSet[GameElement]]): Int = {
      if (of.isEmpty) 0
      else {
        val symbols: MultiSet[Symbol] = of.map(_.symbols).flatten
        val vicSymbols: MultiSet[VictoryPointSymbol] = symbols.filter(_.isInstanceOf[VictoryPointSymbol]).map(_.asInstanceOf[VictoryPointSymbol])
        vicSymbols.map(symbol => calculateAmount(symbol.reward, neighborCards)).sum
      }
    }

    def calculateAmount(reward: Amount, neighborStuff: Map[NeighborReference, MultiSet[GameElement]]): Int = {
      reward match {
        case reward: VariableAmount => {
          // We need to handle references other than Self in a different way
          val fromNeighbors = reward.from.filter(_.isInstanceOf[NeighborReference]).map(_.asInstanceOf[NeighborReference])
          val referencedNeighborStuff: MultiSet[GameElement] = fromNeighbors.map(neighborStuff(_)).fold(MultiSet())(_ ++ _)
          val referencedMyStuff = if (reward.from.contains(Self)) allGameElements else MultiSet()
          val referencedStuff = referencedNeighborStuff ++ referencedMyStuff
          referencedStuff.map( elem => if (elem.getClass == reward.forEach) reward.amount else 0).sum
        }
        case SimpleAmount(amount) => amount
      }
    }

    def canBuild(playable: PlayableElement, availableThroughTrade: Map[NeighborReference, Production]): Boolean = {
      !played.contains(playable) && // You cannot build a card you already own
      (availableEvolutions.contains(playable) || // You can build an evolution whether you can pay it's cost or not
      possibleTrades(playable, availableThroughTrade).nonEmpty)
    }

    def possibleTrades(playable: PlayableElement, tradableProduction: Map[NeighborReference, Production]): Set[Trade] =
      possibleTradesWithoutConsideringCoins(playable, tradableProduction).filter(cost(_).sum <= coins - playable.cost.coins)

    def possibleTradesWithoutConsideringCoins(playable: PlayableElement, tradableProduction: Map[NeighborReference, Production]): Set[Trade] =
      totalProduction.consume(playable.cost.resources).map(possibleTrades(_, tradableProduction)).flatten

    def possibleTrades(resources: MultiSet[Resource],
                       tradableResources: Map[NeighborReference, Production]
                      ): Set[Trade] = {
      if (resources.isEmpty) Set(MultiMap[Resource, NeighborReference]())
      else
        (
          for ((neighboorRef, production) <- tradableResources) yield
            if (production.consumes(resources.head)) {
              val subTrades: Set[Trade] = possibleTrades(resources.tail, tradableResources.updated(neighboorRef, production - resources.head))
              subTrades.map(trade => trade + (resources.head -> neighboorRef))
            }
            else
              Set.empty[Trade]
        ).flatten.toSet
    }

    /**
     * @param trade
     * @return The cost in coins of this trade
     */
    def cost(trade: Trade): (Int, Int) = {
      def cost(trade: Trade, rebates: Set[RebateSymbol]): (Int, Int) =
        if (trade.isEmpty) (0, 0)
        else {
          val (resource, from) = trade.head
          val (relevant, nonRelevant) = rebates.span(rebate => rebate.affectedResources.contains(resource) && rebate.fromWho.contains(from))
          val newRebates = relevant.map(rebate => rebate.copy(multiplicity = rebate.multiplicity - 1)).filter(_.multiplicity > 0) ++ nonRelevant
          val resourceCost = 2 - relevant.size
          if (from == Left) (resourceCost, 0) else (0, resourceCost) + cost(trade.tail, newRebates)
        }
      cost(trade, allSymbols.filter(_.isInstanceOf[RebateSymbol]).map(_.asInstanceOf[RebateSymbol]).toSet)
    }

    def availableEvolutions: Set[Card] = played.map(_.evolutions).flatten

    def +(delta: PlayerDelta) =
      this.copy(coins = coins + delta.coinDelta, played = played ++ delta.newCards, stuff = stuff ++ delta.newBattleMarkers)

    def -(previous: Player): PlayerDelta =
      PlayerDelta(played -- previous.played, coins - previous.coins, battleMarkers -- previous.battleMarkers)
  }

  type Age = Int

  case class Game(players: Circle[Player], cards: Map[Age, MultiSet[Card]], discarded: MultiSet[Card]) {
    def getNeighboorsStuff(player: Player): Map[NeighborReference, MultiSet[GameElement]] = {
      Map(Left -> players.getLeft(player).allGameElements, Right -> players.getRight(player).allGameElements)
    }

    def playTurn(actions: Map[Player, Action]): Game = {
      val deltas = for ( (player, action) <- actions) yield
        action.perform(this, player)

      val newGameState = this + deltas.reduce(_ + _)

      // We go through every played card and resolve it's effect (add coins to players who played a card that rewards in coins)
      val gameStateAfterResolvingCards = actions.foldLeft(newGameState) {
        (gameState, keyValue) =>
          keyValue match {
            case (player, Build(card, trade)) => card.symbols.foldLeft(gameState){ case (gameState, symbol) => symbol.resolve(gameState, player)}
            case (player, BuildWonderStage(card, trade)) => player.wonderStagesBuilt.last.symbols.foldLeft(gameState){ case (gameState, symbol) => symbol.resolve(gameState, player)}
            case _ => gameState
          }
      }
      // During age I and III, we pass our hands to the player to our left, during age II, we pass our hand to the right
      val passHandLeft = currentAge == 1 || currentAge == 3
      // Let's pass the hands left or right
      val nextTurnPlayers: Circle[Player] = players.map[Player](player => if (passHandLeft) player.copy(hand = player.left.hand) else player.copy(hand = player.right.hand))

      val finalGameState = gameStateAfterResolvingCards.copy(players = nextTurnPlayers)

      // Was this the last turn of this age?
      if (finalGameState.players.head.hand.size == 1){
        // Was this the last age?
        if (currentAge == 3)
          finalGameState.endAge()
        else
          finalGameState.endAge().beginAge()
      }
      else
        finalGameState
    }

    def currentAge = cards.keys.toList.reverse.find(cards(_).isEmpty).getOrElse(0)

    def beginAge(): Game = {
      val shuffledNextAgeCards = Random.shuffle(cards(currentAge + 1).toList)
      val hands: Iterator[MultiSet[Card]] = shuffledNextAgeCards.grouped(7).map(_.toMultiSet)
      // Let's give each player his new hand as well as indicate that they have all not built for free this age yet
      val updatedPlayers = players.map[Player]{ player => player.copy(hand = hands.next(), hasBuiltForFreeThisAge = false) }
      Game(updatedPlayers, cards.updated(currentAge, MultiSet()), discarded)
    }

    def endAge(): Game = {
      val winMarker = currentAge match { case 1 => VictoryBattleMarker(1) case 2 => VictoryBattleMarker(3) case 3 => VictoryBattleMarker(5)}
      val playersAtWar = players.filter(!_.hasDiplomacy)
      val playerDeltas =
        if (playersAtWar.size == 2)
          playersAtWar.createMap{
            player =>
              val battleMarker =
                player.militaryStrength.compare(player.left.militaryStrength) match {
                  case 0 => MultiSet[BattleMarker]()
                  case 1 => MultiSet[BattleMarker](winMarker)
                  case -1 => MultiSet[BattleMarker](new DefeatBattleMarker)
                }
              PlayerDelta(Set(), 0, battleMarker)
          }
        else
          playersAtWar.createMap{
            player =>
              val leftBattleMarker =
                player.militaryStrength.compare(player.left.militaryStrength) match {
                  case 0 => MultiSet[BattleMarker]()
                  case 1 => MultiSet(winMarker)
                  case -1 => MultiSet(new DefeatBattleMarker)
                }

              val rightBattleMarker =
                player.militaryStrength.compare(player.right.militaryStrength) match {
                  case 0 => MultiSet[BattleMarker]()
                  case 1 => MultiSet(winMarker)
                  case -1 => MultiSet(new DefeatBattleMarker)
                }

            PlayerDelta(Set(), 0, leftBattleMarker ++ rightBattleMarker)
          }
      val discards = players.map[MultiSet[Card]](_.hand).reduce(_ ++ _)
      val afterWarAndDiscard = this + GameDelta(playerDeltas, discards)
      // Let's remove one diplomacyToken from all players (if they don't have one they stay at zero)
      afterWarAndDiscard.copy(players = players.map[Player](_.removeDiplomacyToken))
    }

    def +(delta: GameDelta): Game = {
      val updatedPlayers = players.map[Player]{
        player =>
          val playerDelta = delta.playerDeltas(player)
          player.copy(coins = player.coins + playerDelta.coinDelta, played = player.played ++ playerDelta.newCards)
      }
      Game(updatedPlayers, cards, discarded ++ delta.additionalDiscards)
    }
  }

  case class GameDelta(playerDeltas: Map[Player, PlayerDelta], additionalDiscards: MultiSet[Card] = MultiSet()) {
    def +(other: GameDelta): GameDelta = {
      val newPlayerDeltas: Map[Player, PlayerDelta] = playerDeltas.map{case (player, delta) => (player, other.playerDeltas(player) + delta)}
      val totalDiscards = additionalDiscards ++ other.additionalDiscards
      GameDelta(newPlayerDeltas, totalDiscards)
    }
    def +(player: Player, other: PlayerDelta): GameDelta =
      GameDelta(playerDeltas.updated(player, playerDeltas(player) + other), additionalDiscards)
  }

  case class PlayerDelta(newCards: Set[Card], coinDelta: Int, newBattleMarkers: MultiSet[BattleMarker]) {
    def +(other: PlayerDelta): PlayerDelta =
      PlayerDelta(newCards ++ other.newCards, coinDelta + other.coinDelta, newBattleMarkers ++ other.newBattleMarkers)
  }

  trait Action {
    def perform(current:Game, by: Player): GameDelta
  }
  case class Build(card: Card, trade: Trade) extends Action {
    def perform(current:Game, by: Player) = {
      val (newPlayer, coinsToGive) = by.build(card, trade)
      val left = current.players.getLeft(by)
      val right = current.players.getRight(by)
      val leftDelta = PlayerDelta(Set(), coinsToGive._1, MultiSet())
      val rightDelta = PlayerDelta(Set(), coinsToGive._2, MultiSet())
      GameDelta(Map(by -> newPlayer.-(by), left -> leftDelta, right -> rightDelta))
    }
  }
  case class Discard(card: Card) extends Action {
    def perform(current:Game, by: Player) =
      GameDelta(Map(by -> by.discard(card).-(by)), MultiSet(card))
  }
  case class BuildWonderStage(card: Card, trade: Trade) extends Action {
    def perform(current:Game, by: Player) = {
      val (newPlayer, coinsToGive) = by.buildWonderStage(card, trade)
      val left = current.players.getLeft(by)
      val right = current.players.getRight(by)
      val leftDelta = PlayerDelta(Set(), coinsToGive._1, MultiSet())
      val rightDelta = PlayerDelta(Set(), coinsToGive._2, MultiSet())
      GameDelta(Map(by -> newPlayer.-(by), left -> leftDelta, right -> rightDelta))
    }
  }
  case class BuildForFree(card: Card) extends Action {
    def perform(current:Game, by: Player) =
      GameDelta(Map(by -> by.buildForFree(card).-(by)))
  }
  case class TwoActions(first: Action, second: Action) extends Action {
    def perform(current:Game, by: Player) =
      if (!by.allSymbols.contains(PlayLastCardEachAge)) throw new UnsupportedOperationException("This player does not have the ability to play this action")
      else if (by.hand.size != 2) throw new UnsupportedOperationException("This ability can only be used when the player has 2 cards left in his hand")
      else {
        val firstDelta = first.perform(current, by)
        firstDelta + second.perform(current + firstDelta, by)
      }
  }

  class BattleMarker(val vicPoints: Int) extends GameElement
  class DefeatBattleMarker extends BattleMarker(-1)
  {
    override def toString = "DefeatBattleMarker"
    override def equals(other: Any) = other match {
      case other: DefeatBattleMarker => true
      case _ => false
    }
  }
  case class VictoryBattleMarker(override val vicPoints: Int) extends BattleMarker(vicPoints)

  class DiplomacyToken extends GameElement {
    override def equals(other: Any) = other match {
      case other: DiplomacyToken => true
      case _ => false
    }
  }
  case class DebtToken(amount: Int) extends GameElement

  type PlayerAmount = Int

  case class GameSetup(allCards: Map[Age, Map[PlayerAmount, MultiSet[Card]]], guildCards: Set[GuildCard]) {
    def generateCards(nbPlayers: Int): Map[Age, MultiSet[Card]] = {
      if (nbPlayers < 3) throw new IllegalArgumentException("You cannot currently build less than three players")
      else {
        // Adding all cards that should be used depending on the amount of players
        val cardsWithoutGuilds =
          allCards.mapValues( cards => (3 to nbPlayers).foldLeft(MultiSet[Card]())((set, key) => set ++ cards(key)))
        // Add 2 + nbPlayers guild cards selected randomly
        cardsWithoutGuilds.updated(3, cardsWithoutGuilds(3).++[Card](setToMultiSet(AugmentedSet(guildCards).takeRandom(nbPlayers + 2))))
      }
    }
  }

  case class Civilization(name: String, base:Production, stagesOfWonder: List[WonderStage])

  ////
  // AGE I
  ////

  // Commercial Cards
  val TAVERN = CommercialCard("TAVERN", Free, Set(), Set(CoinSymbol(SimpleAmount(5))))
  val WEST_TRADING_POST = CommercialCard("WEST TRADING POST", Free, Set(FORUM), Set(RebateSymbol(Set(Clay, Stone, Wood, Ore), Set(Left))))
  val MARKETPLACE = CommercialCard("MARKETPLACE", Free, Set(CARAVANSERY), Set(RebateSymbol(Set(Glass, Tapestry, Paper), Set(Left, Right))))
  val EAST_TRADING_POST = CommercialCard("EAST TRADING POST", Free, Set(FORUM), Set(RebateSymbol(Set(Clay, Stone, Wood, Ore), Set(Right))))

  // Military Cards
  val STOCKADE = MilitaryCard("STOCKADE", Cost(0, MultiSet(Wood)), Set(), 1)
  val BARRACKS = MilitaryCard("BARRACKS", Cost(0, MultiSet(Ore)), Set(), 1)
  val GUARD_TOWER = MilitaryCard("GUARD TOWER", Cost(0, MultiSet(Clay)), Set(), 1)

  // Science Cards
  val WORKSHOP = ScienceCard("WORKSHOP", Cost(0, MultiSet(Glass)), Set(LABORATORY, ARCHERY_RANGE), gear)
  val SCRIPTORIUM = ScienceCard("SCRIPTORIUM", Cost(0, MultiSet(Paper)), Set(COURTHOUSE, LIBRARY), tablet)
  val APOTHECARY = ScienceCard("APOTHECARY", Cost(0, MultiSet(Tapestry)), Set(STABLES, DISPENSARY), compass)


  // Civilian Cards
  val THEATER = CivilianCard("THEATER", Free, Set(STATUE), 2)
  val BATHS = CivilianCard("BATHS", Cost(0, MultiSet(Stone)), Set(AQUEDUCT), 3)
  val ALTAR = CivilianCard("ALTAR", Free, Set(TEMPLE), 2)
  val PAWNSHOP = CivilianCard("PAWNSHOP", Free, Set(), 3)

  // Raw Material Cards
  val TREE_FARM = new RawMaterialCard("TREE FARM", new Cost(1), Wood | Clay)
  val MINE = new RawMaterialCard("MINE", new Cost(1), Stone | Ore)
  val CLAY_PIT = new RawMaterialCard("CLAY PIT",new Cost(1), Clay | Ore)
  val TIMBER_YARD = new RawMaterialCard("TIMBER YARD", new Cost(1), Stone | Wood)
  val STONE_PIT = new RawMaterialCard("STONE PIT", Free, Stone)
  val FOREST_CAVE = new RawMaterialCard("FOREST CAVE", new Cost(1), Wood | Ore)
  val LUMBER_YARD = new RawMaterialCard("LUMBER YARD", Free, Wood)
  val ORE_VEIN = new RawMaterialCard("ORE VEIN", Free, Ore)
  val EXCAVATION = new RawMaterialCard("EXCAVATION", new Cost(1), Stone | Clay)
  val CLAY_POOL = new RawMaterialCard("CLAY POOL", Free, Clay)

  // Manufactured Good Cards
  val LOOM = new ManufacturedGoodCard("LOOM", Tapestry)
  val GLASSWORKS = new ManufacturedGoodCard("GLASSWORKS", Glass)
  val PRESS = new ManufacturedGoodCard("PRESS", Paper)

  ////
  // AGE II
  ////

  // Commercial Cards
  val CARAVANSERY = CommercialCard("CARAVANSERY", Cost(0, MultiSet(Wood, Wood)), Set(LIGHTHOUSE), Set(Wood | Stone | Ore | Clay))
  val FORUM = CommercialCard("FORUM", Cost(0, MultiSet(Clay, Clay)), Set(HAVEN), Set(Glass | Tapestry | Paper))
  val BAZAR = CommercialCard("BAZAR", Free, Set(), Set(CoinSymbol(VariableAmount(2, classOf[ManufacturedGoodCard], Set(Left, Self, Right)))))
  val VINEYARD = CommercialCard("VINEYARD", Free, Set(), Set(CoinSymbol(VariableAmount(1, classOf[RawMaterialCard], Set(Left, Self, Right)))))

  // Military Cards
  val WALLS = MilitaryCard("WALLS", Cost(0, MultiSet(Stone, Stone, Stone)), Set(FORTIFICATIONS), 2)
  val ARCHERY_RANGE = MilitaryCard("ARCHERY RANGE", Cost(0, MultiSet(Wood, Wood, Ore)), Set(), 2)
  val TRAINING_GROUND = MilitaryCard("TRAINING GROUND", Cost(0, MultiSet(Ore, Ore, Wood)), Set(CIRCUS), 2)
  val STABLES = MilitaryCard("STABLES", Cost(0, MultiSet(Clay, Wood, Ore)), Set(), 2)

  // Science Cards
  val SCHOOL = ScienceCard("SCHOOL", Cost(0, MultiSet(Wood, Paper)), Set(ACADEMY, STUDY), tablet)
  val LIBRARY = ScienceCard("LIBRARY", Cost(0, MultiSet(Stone, Stone, Tapestry)), Set(SENATE, UNIVERSITY), tablet)
  val LABORATORY = ScienceCard("LABORATORY", Cost(0, MultiSet(Clay, Clay, Paper)), Set(OBSERVATORY, SIEGE_WORKSHOP),gear)
  val DISPENSARY = ScienceCard("DISPENSARY", Cost(0, MultiSet(Ore, Ore, Glass)), Set(LODGE, ARENA), compass)

  // Civilian Cards
  val AQUEDUCT = CivilianCard("AQUEDUC", Cost(0, MultiSet(Stone, Stone, Stone)), Set(), 5)
  val STATUE = CivilianCard("STATUE", Cost(0, MultiSet(Ore, Ore, Wood)), Set(GARDENS), 4)
  val TEMPLE = CivilianCard("TEMPLE", Cost(0, MultiSet(Wood, Clay, Glass)), Set(PANTHEON), 3)
  val COURTHOUSE = CivilianCard("COURTHOUSE", Cost(0, MultiSet(Clay, Clay, Tapestry)), Set(), 4)

  // Raw Material Cards
  val FOUNDRY = new RawMaterialCard("FOUNDRY", new Cost(1), Ore + Ore)
  val QUARRY = new RawMaterialCard("QUARRY", new Cost(1), Stone + Stone)
  val BRICKYARD = new RawMaterialCard("BRICKYARD", new Cost(1), Clay + Clay)
  val SAWMILL = new RawMaterialCard("SAWMILL", new Cost(1), Wood + Wood)

  ////
  // AGE III
  ////

  // Commercial Cards
  val ARENA = CommercialCard("ARENA", Cost(0, MultiSet(Stone, Stone, Ore)), Set(), Set(CoinSymbol(VariableAmount(3, classOf[WonderStage], Set(Self))), VictoryPointSymbol(VariableAmount(1, classOf[WonderStage], Set(Self)))))
  val CHAMBER_OF_COMMERCE = CommercialCard("CHAMBER OF COMMERCE", Cost(0, MultiSet(Clay, Clay, Paper)), Set(), Set(VictoryPointSymbol(VariableAmount(2, classOf[ManufacturedGoodCard], Set(Self))), CoinSymbol(VariableAmount(2, classOf[ManufacturedGoodCard], Set(Self)))))
  val LIGHTHOUSE = CommercialCard("LIGHTHOUSE", Cost(0, MultiSet(Stone, Glass)), Set(), Set(CoinSymbol(VariableAmount(1, classOf[CommercialCard], Set(Self))), VictoryPointSymbol(VariableAmount(1, classOf[CommercialCard], Set(Self)))))
  val HAVEN = CommercialCard("HAVEN", Cost(0, MultiSet(Wood, Ore, Tapestry)), Set(), Set(CoinSymbol(VariableAmount(1, classOf[RawMaterialCard], Set(Self))), VictoryPointSymbol(VariableAmount(1, classOf[RawMaterialCard], Set(Self)))))

  // Military Cards
  val CIRCUS = MilitaryCard("CIRCUS", Cost(0, MultiSet(Stone, Stone, Stone, Ore)), Set(), 3)
  val FORTIFICATIONS = MilitaryCard("FORTIFICATIONS", Cost(0, MultiSet(Ore, Ore, Ore, Stone)), Set(), 3)
  val ARSENAL = MilitaryCard("ARSENAL", Cost(0, MultiSet(Wood, Wood, Ore, Tapestry)), Set(), 3)
  val SIEGE_WORKSHOP = MilitaryCard("SIEGE WORKSHOP", Cost(0, MultiSet(Clay, Clay, Clay, Wood)), Set(), 3)

  // Science Cards
  val OBSERVATORY = ScienceCard("OBSERVATORY", Cost(0, MultiSet(Ore, Ore, Glass, Tapestry)), Set(), gear)
  val ACADEMY = ScienceCard("ACADEMY", Cost(0, MultiSet(Stone, Stone, Stone)), Set(), compass)
  val LODGE = ScienceCard("LODGE", Cost(0, MultiSet(Clay, Clay, Paper, Tapestry)), Set(), compass)
  val UNIVERSITY = ScienceCard("UNIVERSITY", Cost(0, MultiSet(Wood, Wood, Paper, Glass)), Set(), tablet)
  val STUDY = ScienceCard("STUDY", Cost(0, MultiSet(Wood, Paper, Tapestry)), Set(), gear)

  // Civilian Cards
  val TOWN_HALL = CivilianCard("TOWN HALL", Cost(0, MultiSet(Stone, Stone, Ore, Glass)), Set(), 6)
  val PALACE = CivilianCard("PALACE", Cost(0, MultiSet(Stone, Ore, Wood, Clay, Glass, Paper, Tapestry)), Set(), 8)
  val PANTHEON = CivilianCard("PANTHEON", Cost(0, MultiSet(Clay, Clay, Ore, Glass, Paper, Tapestry)), Set(), 7)
  val GARDENS = CivilianCard("GARDENS", Cost(0, MultiSet(Clay, Clay, Wood)), Set(), 5)
  val SENATE = CivilianCard("SENATE", Cost(0, MultiSet(Wood, Wood, Stone, Ore)), Set(), 6)

  // Guilds
  val STRATEGISTS_GUILD = GuildCard("STARTEGISTS GUILD", Cost(0, MultiSet(Ore, Ore, Stone, Tapestry)), Set(VictoryPointSymbol(VariableAmount(1, classOf[DefeatBattleMarker], Set(Left, Right)))))
  val TRADERS_GUILD = GuildCard("TRADERS GUILD", Cost(0, MultiSet(Glass, Tapestry, Paper)), Set(VictoryPointSymbol(VariableAmount(1, classOf[CommercialCard], Set(Left, Right)))))
  val MAGISTRATES_GUILD = GuildCard("MAGISTRATES GUILD", Cost(0, MultiSet(Wood, Wood, Wood, Stone, Tapestry)), Set(VictoryPointSymbol(VariableAmount(1, classOf[CivilianCard], Set(Left, Right)))))
  val SHOPOWNERS_GUILD = GuildCard("SHOPOWNERS GUILD", Cost(0, MultiSet(Wood, Wood, Wood, Glass, Paper)), Set(VictoryPointSymbol(VariableAmount(1, classOf[RawMaterialCard], Set(Self))), VictoryPointSymbol(VariableAmount(1, classOf[ManufacturedGoodCard], Set(Self))), VictoryPointSymbol(VariableAmount(1, classOf[GuildCard], Set(Self)))))
  val CRAFTMENS_GUILD = GuildCard("CRAFTSMENS GUILD", Cost(0, MultiSet(Ore, Ore, Stone, Stone)), Set(VictoryPointSymbol(VariableAmount(2, classOf[ManufacturedGoodCard], Set(Left, Right)))))
  val WORKERS_GUILD = GuildCard("WORKERS GUILD", Cost(0, MultiSet(Ore, Ore, Clay, Stone, Wood)), Set(VictoryPointSymbol(VariableAmount(1, classOf[RawMaterialCard], Set(Left, Right)))))
  val PHILOSOPHERS_GUILD = GuildCard("PHILOSOPHERS GUILD", Cost(0, MultiSet(Clay, Clay, Clay, Paper, Tapestry)), Set(VictoryPointSymbol(VariableAmount(1, classOf[ScienceCard], Set(Left, Right)))))
  val SCIENTISTS_GUILD = GuildCard("SCIENTISTS GUILD", Cost(0, MultiSet(Wood, Wood, Ore, Ore, Paper)), Set(gear | tablet | compass))
  val SPIES_GUILD = GuildCard("SPIES GUILD", Cost(0, MultiSet(Clay, Clay, Clay, Glass)), Set(VictoryPointSymbol(VariableAmount(1, classOf[MilitaryCard], Set(Left, Right)))))
  val BUILDERS_GUILD = GuildCard("BUILDERS GUILD", Cost(0, MultiSet(Stone, Stone, Clay, Clay, Glass)), Set(VictoryPointSymbol(VariableAmount(1, classOf[WonderStage], Set(Left, Self, Right)))))

  // Cities Cards
  val PIGEON_LOFT = CityCard("PIGEON_LOFT", Cost(1, MultiSet(Ore)), Set(new StealScience))
  val SPY_RING = CityCard("SPY_RING", Cost(2, MultiSet(Stone, Clay)), Set(new StealScience))
  val TORTURE_CHAMBER = CityCard("TORTURE_CHAMBER", Cost(3, MultiSet(Glass, Ore, Ore)), Set(new StealScience))
  val CLANDESTINE_DOCK_WEST = CityCard("CLANDESTINE_DOCK_WEST", new Cost(1), Set(RebateSymbol(allResources, Set(Left), 1)))
  val CLANDESTINE_DOCK_EAST = CityCard("CLANDESTINE_DOCK_EAST", new Cost(1), Set(RebateSymbol(allResources, Set(Right), 1)))
  val GAMBLING_DEN = CityCard("GAMBLING_DEN",Free, Set(CoinSymbol(ThreeWayAmount(1, 6, 1))))
  val GAMBLING_HOUSE = CityCard("GAMBLING_HOUSE",new Cost(1), Set(CoinSymbol(ThreeWayAmount(2, 8, 2))))

  // Civilizations
  val RHODOS_A = Civilization("RHODOS", Ore, List(
    WonderStage(Cost(0, MultiSet(Wood, Wood)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Clay, Clay, Clay)), Set(MilitarySymbol(2))),
    WonderStage(Cost(0, MultiSet(Ore, Ore, Ore, Ore)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val RHODOS_B = Civilization("RHODOS", Ore, List(
    WonderStage(Cost(0, MultiSet(Stone, Stone, Stone)), Set(MilitarySymbol(1), VictoryPointSymbol(SimpleAmount(3)), CoinSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Ore, Ore, Ore, Ore)), Set(MilitarySymbol(1), VictoryPointSymbol(SimpleAmount(4)), CoinSymbol(SimpleAmount(4))))
  ))
  val ALEXANDRIA_A = Civilization("ALEXANDRIA", Glass, List(
    WonderStage(Cost(0, MultiSet(Stone, Stone)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Ore, Ore)), Set(Clay | Ore | Wood | Stone)),
    WonderStage(Cost(0, MultiSet(Glass, Glass)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val ALEXANDRIA_B = Civilization("ALEXANDRIA", Glass, List(
    WonderStage(Cost(0, MultiSet(Clay, Clay)), Set(Wood | Stone | Ore | Clay)),
    WonderStage(Cost(0, MultiSet(Wood, Wood)), Set(Glass | Tapestry | Paper)),
    WonderStage(Cost(0, MultiSet(Stone, Stone)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val HALIKARNASSOS_A = Civilization("HALIKARNASSOS", Tapestry, List(
    WonderStage(Cost(0, MultiSet(Clay, Clay)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Ore, Ore, Ore)), Set(GrabFromDiscardPile)),
    WonderStage(Cost(0, MultiSet(Tapestry, Tapestry)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val HALIKARNASSOS_B = Civilization("HALIKARNASSOS", Tapestry, List(
    WonderStage(Cost(0, MultiSet(Ore, Ore)), Set(VictoryPointSymbol(SimpleAmount(2)), GrabFromDiscardPile)),
    WonderStage(Cost(0, MultiSet(Clay, Clay, Clay)), Set(VictoryPointSymbol(SimpleAmount(1)), GrabFromDiscardPile)),
    WonderStage(Cost(0, MultiSet(Glass, Paper, Tapestry)), Set(GrabFromDiscardPile))
  ))
  val OLYMPIA_A = Civilization("OLYMPIA", Wood, List(
    WonderStage(Cost(0, MultiSet(Wood, Wood)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Stone, Stone)), Set(FreeBuildEachAge)),
    WonderStage(Cost(0, MultiSet(Ore, Ore)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val OLYMPIA_B = Civilization("OLYMPIA", Wood, List(
    WonderStage(Cost(0, MultiSet(Wood, Wood)), Set(RebateSymbol(Set(Clay, Stone, Wood, Ore), Set(Left, Right)))),
    WonderStage(Cost(0, MultiSet(Stone, Stone)), Set(VictoryPointSymbol(SimpleAmount(5)))),
    WonderStage(Cost(0, MultiSet(Tapestry, Ore, Ore)), Set(CopyGuildCard))
  ))
  val GIZAH_A = Civilization("GIZAH", Stone, List(
    WonderStage(Cost(0, MultiSet(Stone, Stone)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Wood, Wood, Wood)), Set(VictoryPointSymbol(SimpleAmount(5)))),
    WonderStage(Cost(0, MultiSet(Stone, Stone, Stone, Stone)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val GIZAH_B = Civilization("GIZAH", Stone, List(
    WonderStage(Cost(0, MultiSet(Wood, Wood)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Stone, Stone, Stone)), Set(VictoryPointSymbol(SimpleAmount(5)))),
    WonderStage(Cost(0, MultiSet(Clay, Clay, Clay)), Set(VictoryPointSymbol(SimpleAmount(5)))),
    WonderStage(Cost(0, MultiSet(Tapestry, Stone, Stone, Stone, Stone)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val EPHESOS_A = Civilization("EPHESOS", Paper, List(
    WonderStage(Cost(0, MultiSet(Stone, Stone)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Wood, Wood)), Set(CoinSymbol(SimpleAmount(9)))),
    WonderStage(Cost(0, MultiSet(Paper, Paper)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val EPHESOS_B = Civilization("EPHOSOS", Paper, List(
    WonderStage(Cost(0, MultiSet(Stone, Stone)), Set(VictoryPointSymbol(SimpleAmount(2)))),
    WonderStage(Cost(0, MultiSet(Wood, Wood)), Set(VictoryPointSymbol(SimpleAmount(3)), CoinSymbol(SimpleAmount(4)))),
    WonderStage(Cost(0, MultiSet(Paper, Tapestry, Glass)), Set(VictoryPointSymbol(SimpleAmount(5)), CoinSymbol(SimpleAmount(4))))
  ))
  val BABYLON_A = Civilization("BABYLON", Clay, List(
    WonderStage(Cost(0, MultiSet(Clay, Clay)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Wood, Wood, Wood)), Set(tablet | compass | gear)),
    WonderStage(Cost(0, MultiSet(Clay, Clay, Clay)), Set(VictoryPointSymbol(SimpleAmount(7))))
  ))
  val BABYLON_B = Civilization("BABYLON", Clay, List(
    WonderStage(Cost(0, MultiSet(Tapestry, Clay)), Set(VictoryPointSymbol(SimpleAmount(3)))),
    WonderStage(Cost(0, MultiSet(Glass, Wood, Wood)), Set(PlayLastCardEachAge)),
    WonderStage(Cost(0, MultiSet(Paper, Clay, Clay, Clay)), Set(tablet | compass | gear))
  ))

  val civilizations = Set(RHODOS_A, ALEXANDRIA_A, HALIKARNASSOS_A, OLYMPIA_A, GIZAH_A, EPHESOS_A, BABYLON_A)

  // Game Setup
  val classicSevenWonders = GameSetup(
    Map(
      1 -> Map(
        3 -> MultiSet(APOTHECARY, CLAY_POOL, ORE_VEIN, WORKSHOP, SCRIPTORIUM, BARRACKS, EAST_TRADING_POST, STOCKADE, CLAY_PIT, LOOM, GLASSWORKS, THEATER, BATHS, TIMBER_YARD, PRESS, STONE_PIT, MARKETPLACE, GUARD_TOWER, WEST_TRADING_POST, ALTAR, LUMBER_YARD),
        4 -> MultiSet(GUARD_TOWER, LUMBER_YARD, PAWNSHOP, TAVERN, SCRIPTORIUM, EXCAVATION, ORE_VEIN),
        5 -> MultiSet(CLAY_POOL, ALTAR, APOTHECARY, BARRACKS, STONE_PIT, TAVERN, FOREST_CAVE),
        6 -> MultiSet(THEATER, PRESS, GLASSWORKS, LOOM, MARKETPLACE, MINE, TREE_FARM),
        7 -> MultiSet(WORKSHOP, EAST_TRADING_POST, STOCKADE, BATHS, WEST_TRADING_POST, TAVERN, PAWNSHOP)
      ),
      2 -> Map(
        3 -> MultiSet(CARAVANSERY, VINEYARD, STATUE, ARCHERY_RANGE, DISPENSARY, WALLS, FOUNDRY, LABORATORY, LIBRARY, STABLES, TEMPLE, AQUEDUCT, COURTHOUSE, FORUM, SCHOOL, GLASSWORKS, BRICKYARD, LOOM, QUARRY, SAWMILL, PRESS),
        4 -> MultiSet(BAZAR, TRAINING_GROUND, DISPENSARY, BRICKYARD, FOUNDRY, QUARRY, SAWMILL),
        5 -> MultiSet(GLASSWORKS, COURTHOUSE, LABORATORY, CARAVANSERY, STABLES, PRESS, LOOM),
        6 -> MultiSet(CARAVANSERY, FORUM, VINEYARD, ARCHERY_RANGE, LIBRARY, TEMPLE, TRAINING_GROUND),
        7 -> MultiSet(AQUEDUCT, STATUE, FORUM, BAZAR, SCHOOL, WALLS, TRAINING_GROUND)
      ),
      3 -> Map(
        3 -> MultiSet(LODGE, OBSERVATORY, SIEGE_WORKSHOP, ARENA, SENATE, ARSENAL, ACADEMY, TOWN_HALL, PANTHEON, PALACE, HAVEN, LIGHTHOUSE, UNIVERSITY, GARDENS, FORTIFICATIONS, STUDY),
        4 -> MultiSet(UNIVERSITY, ARSENAL, GARDENS, HAVEN, CIRCUS, CHAMBER_OF_COMMERCE),
        5 -> MultiSet(ARENA, TOWN_HALL, CIRCUS, SIEGE_WORKSHOP, SENATE),
        6 -> MultiSet(TOWN_HALL, CIRCUS, LODGE, PANTHEON, CHAMBER_OF_COMMERCE, LIGHTHOUSE),
        7 -> MultiSet(ARENA, OBSERVATORY, ACADEMY, FORTIFICATIONS, ARSENAL, PALACE)
      )
    ),
    Set(STRATEGISTS_GUILD, TRADERS_GUILD, MAGISTRATES_GUILD, SHOPOWNERS_GUILD, CRAFTMENS_GUILD, WORKERS_GUILD, PHILOSOPHERS_GUILD, SCIENTISTS_GUILD, SPIES_GUILD, BUILDERS_GUILD)
  )
}