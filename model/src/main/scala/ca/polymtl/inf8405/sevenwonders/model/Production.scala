package ca.polymtl.inf8405.sevenwonders.model

import SevenWonders._
import collection.MultiSet

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