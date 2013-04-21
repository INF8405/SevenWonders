package ca.polymtl.inf8405.sevenwonders.model

import utils.Math
import SevenWonders._

trait Symbol {
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
      alt1 <- alternatives
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

object Compass extends SimpleScienceSymbol(1, 0, 0)
object Gear extends SimpleScienceSymbol(0, 1, 0)
object Tablet extends SimpleScienceSymbol(0, 0, 1)

case class MilitarySymbol(strength: Int) extends Symbol
case class VictoryPointSymbol(reward: Amount) extends Symbol
case class CoinSymbol(reward: Amount) extends Symbol {
  override def resolve(current:Game, playedBy: Player): Game = {

    def addToSelf( amount: Int ) = {
      current.copy(players = current.players.replace(playedBy, playedBy.addCoins(amount)))
    }

    reward match {
      case ThreeWayAmount(left, self, right) => {
        val oldLeft = current.players.getLeft(playedBy)
        val newLeft = oldLeft.addCoins(left)
        val oldRight = current.players.getRight(playedBy)
        val newRight = oldRight.addCoins(right)
        val newSelf = playedBy.addCoins(self)
        current.copy(players = current.players.replace(oldLeft, newLeft).replace(oldRight, newRight).replace(playedBy, newSelf))
      }
      case SimpleAmount( amount ) => {
        addToSelf( amount )
      }
      case amount: VariableAmount => {
        addToSelf( playedBy.calculateAmount(amount, current.getNeighboorsStuff(playedBy)))
      }
    }
  }
}
case class RebateSymbol(affectedResources: Set[Resource], fromWho: Set[NeighborReference]) extends Symbol
case class DiscountSymbol(affectedResources: Set[Resource], fromWho: Set[NeighborReference], multiplicity: Int = 1) extends Symbol
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
    val newPlayers =
      current.players.map[Player]( node => {
        val player = node.self

        if (player == playedBy) player
        else {

          amount match {

            case SimpleAmount( amount ) => player.removeCoins(amount)
            case amount:VariableAmount => player.removeCoins(player.calculateAmount(amount, current.getNeighboorsStuff(player)))
            case amount:ThreeWayAmount => sys.error("three way amount for a pay bank ?")
          }

          //
        }
      })

    current.copy( players = newPlayers )
  }
}