import org.specs2.mutable._
import utils.Utils
import Utils._

class UtilsSpec extends Specification {
  "shifLeft" should {
    "return a new List with all element shifted by one to the left" in {
      List(1,2,3,4).shiftLeft === List(2,3,4,1)
    }
  }

  "shiftRight" should {
    "return a new List with all elements shifter by one to the right" in {
      List(1,2,3,4).shiftRight === List(4,1,2,3)
    }
  }

  "Augmented Tuple2" should {
    "support adding elements with an implicit num" in {
      (1,2) + (3,6) ==== (4,8)
    }
  }
}
