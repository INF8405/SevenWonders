package ca.polymtl.inf8405.sevenwonders.model
package collection

import org.specs2.mutable._

class MultiMapSpec extends Specification {
  "MultiMap" should {
    "head should complement tail" in {
      val test = MultiMap("1" -> "Guy", 5 -> "Gilles")
      test.tail + test.head ==== test

      val test1 = test + ("3" -> "Mary")
      test1.tail + test1.head ==== test1
    }

    "isEmpty" in {
      val test = MultiMap(1 -> "Hahaha", 3 -> "ffff")
      test.isEmpty ==== false

      test.tail.isEmpty ==== false
      test.tail.tail.isEmpty ==== true
    }

    "support duplicates" in {
      val test = MultiMap(1 -> 5, 1 -> 5)
      test.size ==== 2
    }

    "tail" in {
      val test = MultiMap(1 -> 5, 1 -> 5)
      test.tail ==== MultiMap(1 -> 5)
    }

    "toMap" in {
      val test = MultiMap(1 -> 5, 1 -> 5)
      test.toMap === Map( 1 -> List( 5, 5 ) )
    }
  }
}
