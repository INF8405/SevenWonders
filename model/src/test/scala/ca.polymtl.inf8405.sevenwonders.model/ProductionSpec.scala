package ca.polymtl.inf8405.sevenwonders.model

import org.specs2.mutable._

import SevenWonders._
import CardCollection._
import CivilizationCollection._

import collection.MultiSet

class ProductionSpec extends Specification {

  "A CumulativeProduction" should {
    "support adding another CumulativeProduction" in {
      val first = new CumulativeProduction(Clay)
      val second = CumulativeProduction(MultiSet(Wood, Wood))
      val actual = first + second
      val expected = CumulativeProduction(MultiSet(Clay, Wood, Wood))
      actual === expected
    }

    "support adding an OptionalProduction" in {
      val first = CumulativeProduction(MultiSet(Clay))
      val second = OptionalProduction(Set(new CumulativeProduction(Wood), new CumulativeProduction(Stone)))
      val actual = first + second
      val expected = OptionalProduction(Set(CumulativeProduction(MultiSet(Clay, Wood)), CumulativeProduction(MultiSet(Clay, Stone))))
      actual === expected
    }

    "support the == oprator to compare resources without attention to order" in {
      val first = CumulativeProduction(MultiSet(Clay, Wood))
      val second = CumulativeProduction(MultiSet(Wood, Clay))
      first === second
    }

    "support the consumes opreration that returns the amount of resources left unpaid" in {
      val prod = CumulativeProduction(MultiSet(Ore, Stone))
      val actual = prod.consume(MultiSet[Resource](Ore, Ore, Stone, Stone, Wood))
      val expected = Set(MultiSet(Ore, Stone, Wood))
      actual === expected
    }

    "support the | operator" in {
      val first = new CumulativeProduction(Wood)
      val second = new CumulativeProduction(Ore)
      val actual = first | second
      val expected = OptionalProduction(Set(first, second))
      actual === expected

      val third = new CumulativeProduction(Stone)
      val fourth = new CumulativeProduction(Clay)
      val actual1 = first | second | third | fourth
      val expected1 = OptionalProduction(Set(first, second, third, fourth))
      actual1 === expected1
    }

    "canProduce" in {
      CumulativeProduction(MultiSet(Wood, Wood, Clay, Glass)).canProduce === Set(Wood, Clay, Glass)
    }

    "cannotProduce" in {
      CumulativeProduction(MultiSet(Wood, Wood, Clay, Glass)).cannotProduce === Set(Ore, Stone, Paper, Tapestry)
    }
  }

  "An OptionalProduction" should {
    "support adding a CumulativeProduction" in {
      val first = OptionalProduction(Set(new CumulativeProduction(Wood), new CumulativeProduction(Stone)))
      val second = CumulativeProduction(MultiSet(Clay))
      val actual = first + second
      val expected = OptionalProduction(Set(CumulativeProduction(MultiSet(Clay, Wood)), CumulativeProduction(MultiSet(Clay, Stone))))
      actual === expected
    }

    "support adding another OptionalProduction" in {
      val first = OptionalProduction(Set(new CumulativeProduction(Clay), new CumulativeProduction(Ore)))
      val second = OptionalProduction(Set(new CumulativeProduction(Wood), new CumulativeProduction(Stone)))
      val actual = first + second
      val expected = OptionalProduction(Set(CumulativeProduction(MultiSet(Clay, Wood)),
					    CumulativeProduction(MultiSet(Clay, Stone)),
					    CumulativeProduction(MultiSet(Ore, Wood)),
					    CumulativeProduction(MultiSet(Ore, Stone))))
      actual === expected
    }

    "support the == oprator to compare resources without attention to order" in {
      val first = OptionalProduction(Set(new CumulativeProduction(Clay), new CumulativeProduction(Ore)))
      val second = OptionalProduction(Set(new CumulativeProduction(Ore), new CumulativeProduction(Clay)))
      assert(first == second)
    }

    "support the consumes opreration that returns the amount of resources left unpaid" in {
      val prod = OptionalProduction(Set(new CumulativeProduction(Clay), new CumulativeProduction(Ore)))
      val actual = prod.consume(MultiSet[Resource](Ore, Ore, Stone, Stone, Wood))
      val expected = Set(MultiSet(Ore, Ore, Stone, Stone, Wood), MultiSet(Ore, Stone, Stone, Wood))
      actual === expected
    }

    "canProduce" in {
      ((Wood | (Stone + Clay)) + Tapestry).canProduce === Set(Wood, Stone, Clay, Tapestry)
    }

    "cannotProduce" in {
      ((Wood | (Stone + Clay)) + Tapestry).cannotProduce === Set(Ore, Glass, Paper)
    }
  }
}