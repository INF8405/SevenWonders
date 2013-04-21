package ca.polymtl.inf8405.sevenwonders.model

import scala.language.implicitConversions

sealed trait Resource
sealed trait RawMaterial extends Resource
sealed trait ManufacturedGood extends Resource

object Resource {
  val rawMaterials = Set(Clay, Wood, Ore, Stone)
  val manufacturedGoods = Set(Glass, Paper, Tapestry)
  val allResources = rawMaterials ++ manufacturedGoods

  implicit def ResourceToProduction(value: Resource) = new CumulativeProduction(value)

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
}