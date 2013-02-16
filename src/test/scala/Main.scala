import org.scalatest._
import com.github.jedesah

class ProductionSpec extends FlatSpec with ShouldMatchers {

  "A CumulativeProduction" should "support adding another CumulativeProduction" in {
    val first = CumulativeProduction(List(Clay))
    val second = CumulativeProduction(List(Wood, Wood))
    val actual = first + second
    val expected = CumulativeProduction(List(Clay, Wood, Wood))
    actual should equal (expected)
  }

  it should "support adding a OptionalProduction" in {
    val first = CumulativeProduction(List(Clay))
    val second = OptionalProduction(List(CumulativeProduction(Wood), CumulativeProduction(Stone)))
    val actual = first + second
    val expected = OptionalProduction(List(CumulativeProduction(Clay, Wood), CumulativeProduction(Clay, Stone)))
    actual should equal (expected)
  }

  it should "support the == oprator to compare resources without attention to order" in {
    val first = CumulativeProduction(List(Clay, Wood))
    val second = CumulativeProduction(List(Wood, Clay))
    first should equal (second)
  }

  it should "support the consume opreration that returns the amount of resources left unpaid" in {
    val prod = CumulativeProduction(List(Ore, Stone))
    val actual = prod.consume(Map(Ore -> 2, Stone -> 2, Wood -> 1))
    val expected = List(Map(Ore -> 1, Stone -> 1, Wood -> 1))
    actual should equal (expected)
  }

  "An OptionalProduction" should "support adding a CumulativeProduction" in {
    val first = OptionalProduction(List(CumulativeProduction(Wood), CumulativeProduction(Stone)))
    val second = CumulativeProduction(List(Clay))
    val actual = first + second
    val expected = OptionalProduction(List(CumulativeProduction(Clay, Wood), CumulativeProduction(Clay, Stone)))
    actual should equal (expected)
  }

  it should "support adding another OptionalProduction" in {
    val first = OptionalProduction(List(CumulativeProduction(Clay), CumulativeProduction(Ore)))
    val second = OptionalProduction(List(CumulativeProduction(Wood), CumulativeProduction(Stone)))
    val actual = first + second
    val expected = OptionalProduction(List(CumulativeProduction(Clay, Wood),
					   CumulativeProduction(Clay, Stone),
					   CumulativeProduction(Ore, Wood),
					   CumulativeProduction(Ore, Stone)))
    actual should equal (expected)
  }

  it should "support the == oprator to compare resources without attention to order" in {
    val first = OptionalProduction(List(CumulativeProduction(Clay), CumulativeProduction(Ore)))
    val second = OptionalProduction(List(CumulativeProduction(Ore), CumulativeProduction(Clay)))
    first should equal (second)
  }

  it should "support the consume opreration that returns the amount of resources left unpaid" in {
    val prod = OptionalProduction(List(CumulativeProduction(Clay), CumulativeProduction(Ore)))
    val actual = prod.consume(Map(Ore -> 2, Stone -> 2, Wood -> 1))
    val expected = List(Map(Ore -> 2, Stone -> 2, Wood -> 1), Map(Ore -> 1, Stone -> 2, Wood -> 1))
    actual should equal (expected)
  }
}