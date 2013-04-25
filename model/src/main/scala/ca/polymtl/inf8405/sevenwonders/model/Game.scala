package ca.polymtl.inf8405.sevenwonders.model

import collection.{MultiSet, Circle}
import collection.conversions._

import scala.util.Random

case class Game(
  players: Circle[Player],
  cards: Map[Age, MultiSet[Card]],
  discarded: MultiSet[Card] = MultiSet(),
  needsToGrabFromDiscardPile: Option[Player] = None,
  needsToPlayLastCard: Option[Player] = None)
{
  import utils.Utils._

  def getNeighboorsStuff(player: Player): Map[NeighborReference, MultiSet[GameElement]] = {
    Map(Left -> players.getLeft(player).allGameElements, Right -> players.getRight(player).allGameElements)
  }

  def getNeighborProductions( player: Player ) = {
    Map(
      Left -> players.getLeft( player ).tradableProduction,
      Right -> players.getRight( player ).tradableProduction
    )
  }

  def findPlayer( by: Civilization ) = {
    players.find( _.civilization == by ).get
  }

  def possibleWonderTrades( player: Player ) =
    player.possibleTrades( player.nextWonderStage, getNeighborProductions(player) )

  def possibleTrades(player: Player, playable: PlayableElement) =
    player.possibleTrades(playable, getNeighborProductions(player))

  def playableCards(player: Player) = player.playableCards(getNeighborProductions(player))

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
          case (actionPlayer, Build(card, trade, wonder)) if !wonder => {
            val player = gameState.findPlayer(actionPlayer.civilization)
            card.resolve(gameState, player)
          }
          case (actionPlayer, Build(card, trade, wonder)) if  wonder => {
            val player = gameState.findPlayer(actionPlayer.civilization)
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
      val nextTurnPlayers: Circle[Player] = players.map[Player](player => if (passHandLeft) player.copy(hand = player.right.hand) else player.copy(hand = player.left.hand))

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
    Game(updatedPlayers, cards.updated(currentAge + 1, MultiSet()), discarded)
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
            PlayerDelta(stuff = battleMarker)
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

            PlayerDelta(stuff = leftBattleMarker ++ rightBattleMarker)
        }
    val discards = players.map[MultiSet[Card]](_.hand).reduce(_ ++ _)
    val afterWarAndDiscard = this + GameDelta(playerDeltas, discards)
    // Let's remove one diplomacyToken from all players (if they don't have one they stay at zero)
    afterWarAndDiscard.copy(players = afterWarAndDiscard.players.map[Player](_.removeDiplomacyToken))
  }

  def +(delta: GameDelta): Game = {
    val updatedPlayers = players.map[Player]{
      player => delta.playerDeltas.get(player).map( player + _ ).getOrElse( player )
    }
    Game(updatedPlayers, cards, discarded ++ delta.additionalDiscards)
  }
}

case class GameDelta(playerDeltas: Map[Player, PlayerDelta], additionalDiscards: MultiSet[Card] = MultiSet()) {
  def +(other: GameDelta): GameDelta = {
    val newPlayerDeltas: Map[Player, PlayerDelta] = {
      // todo: Join two maps
      playerDeltas ++ other.playerDeltas.map{ case ( player, delta) => ( player, playerDeltas.get(player).map( _ + delta).getOrElse(delta) ) }
    }
    val totalDiscards = additionalDiscards ++ other.additionalDiscards
    GameDelta(newPlayerDeltas, totalDiscards)
  }
  def +(player: Player, other: PlayerDelta): GameDelta =
    GameDelta(playerDeltas.updated(player, playerDeltas(player) + other), additionalDiscards)
}

case class PlayerDelta(
  consumed: MultiSet[Card] = MultiSet(),
  newCards: Set[Card] = Set(),
  coinDelta: Int = 0,
  stuff: MultiSet[GameElement] = MultiSet(),
  nbWonder: Int = 0,
  builtForFree: Boolean = false) {

  def +(other: PlayerDelta): PlayerDelta =
    PlayerDelta(
      consumed ++ other.consumed,
      newCards ++ other.newCards,
      coinDelta + other.coinDelta,
      stuff ++ other.stuff,
      nbWonder + other.nbWonder,
      builtForFree || other.builtForFree
    )
}
