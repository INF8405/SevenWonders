import org.specs2.mutable._

class Assumptions extends Specification{
  "1" should {
    "non generic filtering on type works" in {
      val actual = List("hello", 1, "sddf", 7.6, List(1,2,3)).filter(_.isInstanceOf[String])
      actual === List("hello", "sddf")
    }
  }
}
