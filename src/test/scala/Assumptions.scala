import org.specs2.mutable._

class Assumptions extends Specification{
  "1" should {
    "non generic filtering on type works" in {
      val actual = List("hello", 1, "sddf", 7.6, List(1,2,3)).filter(_.isInstanceOf[String])
      actual === List("hello", "sddf")
    }
  }

  "2" should {
    "Map return a normal set for keys" in {
      Map("allo" -> 1, "byebye" -> 1).keySet === Set("allo", "byebye")
    }
  }
}
