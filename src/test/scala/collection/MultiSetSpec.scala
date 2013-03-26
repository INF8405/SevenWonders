package collection

import org.specs2.mutable._

class MultiSetSpec extends Specification {
  "MultiSet" should {
    "toSet" should {
      "without repetition" in {
        MultiSet("allo", "byebye", "kile").toSet === Set("allo", "byebye", "kile")
      }
      "with repetition" in {
        MultiSet("allo", "allo", "byebye", "kile").toSet === Set("allo", "byebye", "kile")
      }
    }

    "toString" should {
      "without repetition" in {
        /*MultiSet("allo", "byebye", "kile").toString === "MultiSet(allo, byebye, kile)" or*/
        MultiSet("allo", "byebye", "kile").toString === "MultiSet(allo, kile, bybye)" or
          MultiSet("allo", "byebye", "kile").toString === "MultiSet(kile, byebye, allo)" /*or
        MultiSet("allo", "byebye", "kile").toString === "MultiSet(kile, allo, bybye)" or
        MultiSet("allo", "byebye", "kile").toString === "MultiSet(bybye, allo, kile)" or
        MultiSet("allo", "byebye", "kile").toString === "MultiSet(byebye, kile, allo)"*/
      }
      "with repetition" in {
        MultiSet("allo", "allo", "byebye", "kile").toString === "MultiSet(byebye, kile, allo, allo)" or
          MultiSet("allo", "allo", "byebye", "kile").toString === "MultiSet(kile, byebye, allo, allo)"
      }
    }

    "mkString" should {
      "without repetition" in {
        MultiSet("allo", "byebye", "kile").mkString("|") === "allo|byebye|kile" or
          MultiSet("allo", "byebye", "kile").mkString("|") === "kile|byebye|allo"
      }
      "with repetition" in {
        MultiSet("allo", "allo", "byebye", "kile").mkString("|") === "allo|allo|byebye|kile" or
          MultiSet("allo", "allo", "byebye", "kile").mkString("|") === "kile|byebye|allo|allo"
      }
    }

    "map" should {
      "without repetition" in {
        MultiSet("allo", "byebye", "kile").map(_.size) === MultiSet(4,6,4)
      }
      "with repetition" in {
        MultiSet("allo", "allo", "byebye", "kile").map(_.size) === MultiSet(4,4,6,4)
      }
    }

    "==" should {
      "without repetition" in {
        MultiSet(4,5,6) === MultiSet(4,5,6)
      }
      "with repetition" in {
        MultiSet(4,5,6,6) === MultiSet(4,5,6,6)
      }
      "out of order" in {
        MultiSet(4,5,6) === MultiSet(6,5,4)
      }
    }

    "head and tail should be complementary" in {
      val test = MultiSet("allo", "byebye", "kile")
      (test.tail + test.head) === test
    }

    "head" in {
      MultiSet("allo", "byebye").head === "allo" or
        MultiSet("allo", "byebye").head === "byebye"
    }

    "tail" in {
      MultiSet("allo", "byebye").tail === MultiSet("allo") or
        MultiSet("allo", "byebye").tail === MultiSet("byebye")
    }

    "+" in {
      MultiSet("allo", "byebye") + "ddd" === MultiSet("allo", "byebye", "ddd")
    }
  }
}
