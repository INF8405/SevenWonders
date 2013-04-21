package ca.polymtl.inf8405.sevenwonders.model

import collection.MultiSet

import scala.language.implicitConversions
import scala.language.existentials

sealed trait GameElement

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
) extends PlayableElement {

  override def toString = name
}

case class Cost(coins: Int, resources: MultiSet[Resource]) {
  def this(resources: MultiSet[Resource]) = this(0, resources)
  def this(coins: Int) = this(coins, MultiSet())
}

object Free extends Cost(0)

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

object Amount {
  implicit def IntToSimpleAmount(value: Int) = SimpleAmount(value)
}

sealed trait Amount
case class SimpleAmount(value: Int) extends Amount
case class VariableAmount(
  amount: Int,
  forEach: Class[_ <: GameElement],
  from: Set[PlayerReference]
) extends Amount
case class ThreeWayAmount(left: Int, self: Int, right: Int) extends Amount

case class WonderStage(cost: Cost, symbols: Set[Symbol]) extends PlayableElement