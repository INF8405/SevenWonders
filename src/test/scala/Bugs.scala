import collection.MultiSet
import com.github.jedesah.SevenWonders._
import org.specs2.mutable._

class Bugs extends Specification {
  "#1" should {
    CumulativeProduction(MultiSet(Wood, Wood, Clay)) === CumulativeProduction(MultiSet(Clay, Wood, Wood))
  }
}
