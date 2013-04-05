package collection

import org.specs2.mutable._
import collection.conversions.unboxCircleNode

class CircleSpec extends Specification {
  "Circle" should {
    "CircleNode" in {
      val test = new Circle(1.0, 2.0, 3.0, 4.0)
      val actual: Circle[Double] = test.map[Double](elem => elem.left + elem.right + elem)
      //val actual: Circle[Double] = test.map(elem => elem + 1)
      actual === new Circle(7.0, 6.0, 9.0, 8.0)
    }
  }
}
