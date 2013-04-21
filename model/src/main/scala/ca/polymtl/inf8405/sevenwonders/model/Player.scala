package ca.polymtl.inf8405.sevenwonders.model

import collection.{MultiMap, MultiSet}

import CardCollection._
import Production._

sealed trait PlayerReference
sealed trait NeighborReference extends PlayerReference
object Left extends NeighborReference
object Right extends NeighborReference
object Self extends PlayerReference

case class Civilization(name: String, base:Production, stagesOfWonder: List[WonderStage])

case class Player(
  civilization: Civilization,
  hand: MultiSet[Card] = MultiSet(),
  coins: Int = 0,
  stuff: MultiSet[GameElement] = MultiSet(),
  played: Set[Card] = Set(),
  nbWonders: Int = 0,
  hasBuiltForFreeThisAge: Boolean = false ) {

  import utils.Utils._

  def discard(card: Card): Player = this.copy(hand = hand - card, coins = coins + 3)

  def build(card: Card, trade: Trade, wonder: Boolean = false) =
    if (wonder) buildWonderStage(card, trade)
    else buildNormal(card, trade)

  /**
   * Handles all state changing relative to this player when he plays a card.
   * @param card The card to build
   * @param trade The trade used to build this card. Can be an empty trade
   * @return The updated Player state along with the amount of coins given to the left and right players
   */
  def buildNormal(card: Card, trade: Trade): (Player, (Int, Int)) = {
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
    else canBuild(nextWonderStage, availableThroughTrade)

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

  def nextWonderStage: WonderStage = civilization.stagesOfWonder( nbWonders )

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
      vicSymbols.map( _.reward match {
        case SimpleAmount( amount ) => amount
        case amount: VariableAmount => calculateAmount(amount, neighborCards)
        case amount: ThreeWayAmount => amount.self
      }).sum
    }
  }

  def calculateAmount(reward: VariableAmount, neighborStuff: Map[NeighborReference, MultiSet[GameElement]]): Int = {
    // We need to handle references other than Self in a different way
    val fromNeighbors = reward.from.filter(_.isInstanceOf[NeighborReference]).map(_.asInstanceOf[NeighborReference])
    val referencedNeighborStuff: MultiSet[GameElement] = fromNeighbors.map(neighborStuff(_)).fold(MultiSet())(_ ++ _)
    val referencedMyStuff = if (reward.from.contains(Self)) allGameElements else MultiSet()
    val referencedStuff = referencedNeighborStuff ++ referencedMyStuff

    referencedStuff.map( elem => if (elem.getClass == reward.forEach) reward.amount else 0).sum
  }

  def canBuild(playable: PlayableElement, availableThroughTrade: Map[NeighborReference, Production]): Boolean = {
    import collection.conversions._

    !played.contains(playable) && // You cannot build a card you already own
      (availableEvolutions.contains(playable) || // You can build an evolution whether you can pay it's cost or not
        possibleTradesWithEmptyTrade(playable, availableThroughTrade).nonEmpty)
  }

  def possibleTrades(playable: PlayableElement, tradableProduction: Map[NeighborReference, Production]): Set[Trade] = {
    possibleTradesWithEmptyTrade( playable, tradableProduction ).remove( emptyTrade )
  }

  def possibleTradesWithEmptyTrade(playable: PlayableElement, tradableProduction: Map[NeighborReference, Production]): Set[Trade] =
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
   * @return A pair containing the left and right cost of a given Trade
   */
  def cost(trade: Trade): (Int, Int) = {
    val rebates = allSymbols.filter(_.isInstanceOf[RebateSymbol]).map(_.asInstanceOf[RebateSymbol]).toSet
    def cost(trade: Trade, discounts: Set[DiscountSymbol]): (Int, Int) =
      if (trade.isEmpty) (0, 0)
      else {
        val (resource, from) = trade.head
        val (relevant, nonRelevant) = discounts.span(discount => discount.affectedResources.contains(resource) && discount.fromWho.contains(from))
        val newDiscounts = relevant.map(discount => discount.copy(multiplicity = discount.multiplicity - 1)).filter(_.multiplicity > 0) ++ nonRelevant
        val anyRelevantRebate = rebates.exists(rebate => rebate.affectedResources.contains(resource) && rebate.fromWho.contains(from))
        val resourceCostAfterRebates = if (anyRelevantRebate) 1 else 2
        val resourceCostAfterDiscounts = resourceCostAfterRebates - relevant.size
        val costThisResource = if (from == Left) (resourceCostAfterDiscounts, 0) else (0, resourceCostAfterDiscounts)
        costThisResource + cost(trade.tail, newDiscounts)
      }
    cost(trade, allSymbols.filter(_.isInstanceOf[DiscountSymbol]).map(_.asInstanceOf[DiscountSymbol]).toSet)
  }

  def availableEvolutions: Set[Card] = played.map(_.evolutions).flatten

  def +(delta: PlayerDelta) =
    this.copy(
      coins = coins + delta.coinDelta,
      played = played ++ delta.newCards,
      stuff = stuff ++ delta.additionalStuff,
      hand = hand -- delta.discards,
      nbWonders = nbWonders + delta.additionalWonders,
      hasBuiltForFreeThisAge = hasBuiltForFreeThisAge || delta.builtForFree
    )

  def -(previous: Player): PlayerDelta =
    PlayerDelta(
      newCards = played -- previous.played,
      coinDelta = coins - previous.coins,
      additionalStuff = stuff -- previous.stuff,
      discards = previous.hand -- hand,
      additionalWonders = nbWonders - previous.nbWonders,
      builtForFree = hasBuiltForFreeThisAge && !previous.hasBuiltForFreeThisAge
    )
}