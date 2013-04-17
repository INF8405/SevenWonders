package ca.polymtl.inf8405.sevenwonders.model

import scala.language.implicitConversions
import scala.language.existentials

import util.Random
import utils.Utils
import Utils._

import collection.MultiMap
import collection.MultiSet
import collection.Circle
import collection.conversions._

object SevenWonders 
{
  import CardCollection._
  import CivilizationCollection._

  val NoProduction = CumulativeProduction(MultiSet[Resource]())

  def beginGame( nbPlayers: Int , playWithCities: Boolean = false): Game = {
    val cards =
      if (playWithCities)
        citySevenWonders.generateCards(nbPlayers)
      else
        classicSevenWonders.generateCards(nbPlayers)
    val qualifiedCivs =
      if (playWithCities) baseCivilizations ++ cityCivilizations
      else baseCivilizations

    val chosenCivilizations = Random.shuffle(qualifiedCivs.toList).take(nbPlayers)
    val players = chosenCivilizations.map{
      civ =>
        Player(civ.takeRandom, MultiSet(), 3)
    }
    Game(new Circle[Player](players: _*), cards, MultiSet()).beginAge()
  }

  case class Cost(coins: Int, resources: MultiSet[Resource]) {
    def this(resources: MultiSet[Resource]) = this(0, resources)
    def this(coins: Int) = this(coins, MultiSet())
  }
  object Free extends Cost(0)

  trait GameElement

  trait PlayableElement extends GameElement {
    val cost: Cost
    val symbols: Set[Symbol]
    def resolve(current:Game, playedBy: Player): Game =
      symbols.foldLeft(current){ case (gameState, symbol) => symbol.resolve(gameState, playedBy) }
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

  sealed trait Amount
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
  implicit def IntToSimpleAmount(value: Int) = SimpleAmount(value)

  type Trade = MultiMap[Resource, NeighborReference]

  case class Player(
    civilization: Civilization,
    hand: MultiSet[Card] = MultiSet(),
    coins: Int = 0,
    stuff: MultiSet[GameElement] = MultiSet(),
    played: Set[Card] = Set(),
    nbWonders: Int = 0,
    hasBuiltForFreeThisAge: Boolean = false ) {

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
        discards = hand -- previous.hand,
        additionalWonders = nbWonders - previous.nbWonders,
        builtForFree = hasBuiltForFreeThisAge && !previous.hasBuiltForFreeThisAge
      )
  }

  type Age = Int

  case class Game(
    players: Circle[Player],
    cards: Map[Age, MultiSet[Card]],
    discarded: MultiSet[Card],
    needsToGrabFromDiscardPile: Option[Player] = None,
    needsToPlayLastCard: Option[Player] = None)
  {
    def getNeighboorsStuff(player: Player): Map[NeighborReference, MultiSet[GameElement]] = {
      Map(Left -> players.getLeft(player).allGameElements, Right -> players.getRight(player).allGameElements)
    }

    def playTurn(actions: Map[Player, Action]): Game = {
      val deltas = for ( (player, action) <- actions) yield
        action.perform(this, player)

      val newGameState = this + deltas.reduce(_ + _)

      var grabDiscard: Option[Player] = None
      var playLast: Option[Player] = None

      // We go through every played card and resolve it's effect (add coins to players who played a card that rewards in coins)
      val gameStateAfterResolvingCards = actions.foldLeft(newGameState) {
        (gameState, keyValue) =>
          keyValue match {
            case (player, Build(card, trade, false)) => card.resolve(gameState, player)
            case (player, Build(card, trade, true)) => {
              val wonderStage = player.wonderStagesBuilt.last
              if (wonderStage.symbols.contains(GrabFromDiscardPile)) grabDiscard = Some(player)
              if (wonderStage.symbols.contains(PlayLastCardEachAge)) playLast = Some(player)
              wonderStage.resolve(gameState, player)
            }
            case _ => gameState
          }
      }

      if (grabDiscard.isDefined || playLast.isDefined)
        gameStateAfterResolvingCards.copy(needsToGrabFromDiscardPile = grabDiscard, needsToPlayLastCard = playLast)
      else
        gameStateAfterResolvingCards.endTurnUpkeep
    }

    def endTurnUpkeep = {
      // Was this the last turn of this age?
      if (players.head.hand.size > 1){
        // During age I and III, we pass our hands to the player to our left, during age II, we pass our hand to the right
        val passHandLeft = currentAge == 1 || currentAge == 3
        // Let's pass the hands left or right
        val nextTurnPlayers: Circle[Player] = players.map[Player](player => if (passHandLeft) player.copy(hand = player.left.hand) else player.copy(hand = player.right.hand))

        this.copy(players = nextTurnPlayers, needsToGrabFromDiscardPile = None, needsToPlayLastCard = None)
      }
      else {
        // Was this the last age?
        if (currentAge == 3)
          endAge()
        else
          endAge().beginAge()
      }
    }

    def grabFromDiscardPile(card: Card): Game = {
      needsToGrabFromDiscardPile match {
        case None => throw new UnsupportedOperationException("No player should be grabing from discard pile in this state")
        case Some(player) => {
          if (discarded.contains(card)) {
            val newPlayers = players.replace(player, player.copy(played = player.played + card))
            val stateAfterResolved = card.resolve(this, player)
            if (needsToPlayLastCard.isDefined)
              stateAfterResolved.copy(players = newPlayers, discarded = discarded - card, needsToGrabFromDiscardPile = None)
            else
              stateAfterResolved.copy(players = newPlayers, discarded = discarded - card).endTurnUpkeep
          }
          else throw new UnsupportedOperationException("Can only grab a card from the discard pile")
        }
      }
    }

    def playLastCardOfAge(action: Action): Game = {
      needsToPlayLastCard match {
        case None => throw new UnsupportedOperationException("No player should be playing the last card of this age")
        case Some(player) => {
          if (needsToGrabFromDiscardPile.isDefined) throw new UnsupportedOperationException("A player needs to grab from the discard pile before this player can play his last card")
          else {
            val stateAferAction = this + action.perform(this, player)
            val stateAfterResolve = action match {
              case Build(card, trade, false) => card.resolve(stateAferAction, player)
              case Build(card, trade, true) => player.wonderStagesBuilt.last.resolve(stateAferAction, player)
            }
            stateAfterResolve.endTurnUpkeep
          }
        }
      }
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
              val battleMarker: MultiSet[GameElement] =
                player.militaryStrength.compare(player.left.militaryStrength) match {
                  case 0 => MultiSet()
                  case 1 => MultiSet(winMarker)
                  case -1 => MultiSet(new DefeatBattleMarker)
                }
              PlayerDelta(additionalStuff = battleMarker)
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

            PlayerDelta(additionalStuff = leftBattleMarker ++ rightBattleMarker)
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

  case class PlayerDelta(discards: MultiSet[Card] = MultiSet(),
                         newCards: Set[Card] = Set(),
                         coinDelta: Int = 0,
                         additionalStuff: MultiSet[GameElement] = MultiSet(),
                         additionalWonders: Int = 0,
                         builtForFree: Boolean = false) {
    def +(other: PlayerDelta): PlayerDelta =
      PlayerDelta(
        discards ++ other.discards,
        newCards ++ other.newCards,
        coinDelta + other.coinDelta,
        additionalStuff ++ other.additionalStuff,
        additionalWonders + other.additionalWonders,
        builtForFree || builtForFree
      )
  }

  trait Action {
    def perform(current:Game, by: Player): GameDelta
  }
  case class Build(card: Card, trade: Trade, wonder: Boolean) extends Action {
    def perform(current:Game, by: Player) = {
      val (newPlayer, coinsToGive) = by.build(card, trade, wonder)
      val left = current.players.getLeft(by)
      val right = current.players.getRight(by)
      val leftDelta = PlayerDelta(coinDelta = coinsToGive._1)
      val rightDelta = PlayerDelta(coinDelta = coinsToGive._2)
      GameDelta(Map(by -> newPlayer.-(by), left -> leftDelta, right -> rightDelta))
    }
  }
  case class Discard(card: Card) extends Action {
    def perform(current:Game, by: Player) =
      GameDelta(Map(by -> by.discard(card).-(by)), MultiSet(card))
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

  class ClassicGameSetup(allCards: Map[Age, Map[PlayerAmount, MultiSet[Card]]], guildCards: Set[GuildCard]) {
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

  class CityGameSetup(allCards: Map[Age, Map[PlayerAmount, MultiSet[Card]]], guildCards: Set[GuildCard], cityCards: Map[Age, Set[CityCard]])
    extends ClassicGameSetup(allCards, guildCards) {
    override def generateCards(nbPlayers: Int): Map[Age, MultiSet[Card]] = {
      val classicSetup = super.generateCards(nbPlayers)
      classicSetup.map {
        case (age, cards) =>
          (age, cards ++ AugmentedSet(cityCards(age)).takeRandom(nbPlayers))
      }
    }
  }

  case class Civilization(name: String, base:Production, stagesOfWonder: List[WonderStage])

  // Game Setup
  val classicSevenWonders = new ClassicGameSetup(normalBaseCards, baseGuilds)
  val citySevenWonders = new CityGameSetup(normalBaseCards, baseGuilds ++ cityGuilds, cityCards)
}