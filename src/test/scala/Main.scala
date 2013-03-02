import org.scalatest._
import com.github.jedesah.SevenWonders._

import com.sidewayscoding.Multiset

class ProductionSpec extends FlatSpec with ShouldMatchers {

  "A CumulativeProduction" should "support adding another CumulativeProduction" in {
    val first = new CumulativeProduction(Clay)
    val second = CumulativeProduction(Multiset(Wood, Wood))
    val actual = first + second
    val expected = CumulativeProduction(Multiset(Clay, Wood, Wood))
    actual should equal (expected)
  }

  it should "support adding a OptionalProduction" in {
    val first = CumulativeProduction(Multiset(Clay))
    val second = OptionalProduction(Set(new CumulativeProduction(Wood), new CumulativeProduction(Stone)))
    val actual = first + second
    val expected = OptionalProduction(Set(CumulativeProduction(Multiset(Clay, Wood)), CumulativeProduction(Multiset(Clay, Stone))))
    actual should equal (expected)
  }

  it should "support the == oprator to compare resources without attention to order" in {
    val first = CumulativeProduction(Multiset(Clay, Wood))
    val second = CumulativeProduction(Multiset(Wood, Clay))
    first should equal (second)
  }

  it should "support the consume opreration that returns the amount of resources left unpaid" in {
    val prod = CumulativeProduction(Multiset(Ore, Stone))
    val actual = prod.consume(Multiset(Ore, Ore, Stone, Stone, Wood))
    val expected = Set(Multiset(Ore, Stone, Wood))
    actual should equal (expected)
  }

  "An OptionalProduction" should "support adding a CumulativeProduction" in {
    val first = OptionalProduction(Set(new CumulativeProduction(Wood), new CumulativeProduction(Stone)))
    val second = CumulativeProduction(Multiset(Clay))
    val actual = first + second
    val expected = OptionalProduction(Set(CumulativeProduction(Multiset(Clay, Wood)), CumulativeProduction(Multiset(Clay, Stone))))
    actual should equal (expected)
  }

  it should "support adding another OptionalProduction" in {
    val first = OptionalProduction(Set(new CumulativeProduction(Clay), new CumulativeProduction(Ore)))
    val second = OptionalProduction(Set(new CumulativeProduction(Wood), new CumulativeProduction(Stone)))
    val actual = first + second
    val expected = OptionalProduction(Set(CumulativeProduction(Multiset(Clay, Wood)),
					   CumulativeProduction(Multiset(Clay, Stone)),
					   CumulativeProduction(Multiset(Ore, Wood)),
					   CumulativeProduction(Multiset(Ore, Stone))))
    actual should equal (expected)
  }

  it should "support the == oprator to compare resources without attention to order" in {
    val first = OptionalProduction(Set(new CumulativeProduction(Clay), new CumulativeProduction(Ore)))
    val second = OptionalProduction(Set(new CumulativeProduction(Ore), new CumulativeProduction(Clay)))
    first should equal (second)
  }

  it should "support the consume opreration that returns the amount of resources left unpaid" in {
    val prod = OptionalProduction(Set(new CumulativeProduction(Clay), new CumulativeProduction(Ore)))
    val actual = prod.consume(Multiset(Ore, Ore, Stone, Stone, Wood))
    val expected = Set(Multiset(Ore, Ore, Stone, Stone, Wood), Multiset(Ore, Stone, Stone, Wood))
    actual should equal (expected)
  }
}