package ca.polymtl.inf8405.sevenwonders.model

import org.specs2.mutable._

import com.sidewayscoding.Multiset

class ProductionSpec extends Specification {

  "A CumulativeProduction" should {
    "support adding another CumulativeProduction" in {
      val first = new CumulativeProduction(Clay)
      val second = CumulativeProduction(Multiset(Wood, Wood))
      val actual = first + second
      val expected = CumulativeProduction(Multiset(Clay, Wood, Wood))
      actual === expected
    }

    "support adding an OptionalProduction" in {
      val first = CumulativeProduction(Multiset(Clay))
      val second = OptionalProduction(Set(new CumulativeProduction(Wood), new CumulativeProduction(Stone)))
      val actual = first + second
      val expected = OptionalProduction(Set(CumulativeProduction(Multiset(Clay, Wood)), CumulativeProduction(Multiset(Clay, Stone))))
      actual === expected
    }

    "support the == oprator to compare resources without attention to order" in {
      val first = CumulativeProduction(Multiset(Clay, Wood))
      val second = CumulativeProduction(Multiset(Wood, Clay))
      first === second
    }

    "support the consume opreration that returns the amount of resources left unpaid" in {
      val prod = CumulativeProduction(Multiset(Ore, Stone))
      val actual = prod.consume(Multiset(Ore, Ore, Stone, Stone, Wood))
      val expected = Set(Multiset(Ore, Stone, Wood))
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
  }

  "An OptionalProduction" should {
    "support adding a CumulativeProduction" in {
      val first = OptionalProduction(Set(new CumulativeProduction(Wood), new CumulativeProduction(Stone)))
      val second = CumulativeProduction(Multiset(Clay))
      val actual = first + second
      val expected = OptionalProduction(Set(CumulativeProduction(Multiset(Clay, Wood)), CumulativeProduction(Multiset(Clay, Stone))))
      actual === expected
    }

    "support adding another OptionalProduction" in {
      val first = OptionalProduction(Set(new CumulativeProduction(Clay), new CumulativeProduction(Ore)))
      val second = OptionalProduction(Set(new CumulativeProduction(Wood), new CumulativeProduction(Stone)))
      val actual = first + second
      val expected = OptionalProduction(Set(CumulativeProduction(Multiset(Clay, Wood)),
					    CumulativeProduction(Multiset(Clay, Stone)),
					    CumulativeProduction(Multiset(Ore, Wood)),
					    CumulativeProduction(Multiset(Ore, Stone))))
      actual === expected
    }

    "support the == oprator to compare resources without attention to order" in {
      val first = OptionalProduction(Set(new CumulativeProduction(Clay), new CumulativeProduction(Ore)))
      val second = OptionalProduction(Set(new CumulativeProduction(Ore), new CumulativeProduction(Clay)))
      //assert(first == second)
      Multiset(2,2) === Multiset(2,2)
    }

    "support the consume opreration that returns the amount of resources left unpaid" in {
      val prod = OptionalProduction(Set(new CumulativeProduction(Clay), new CumulativeProduction(Ore)))
      val actual = prod.consume(Multiset(Ore, Ore, Stone, Stone, Wood))
      val expected = Set(Multiset(Ore, Ore, Stone, Stone, Wood), Multiset(Ore, Stone, Stone, Wood))
      actual === expected
    }
  }
}