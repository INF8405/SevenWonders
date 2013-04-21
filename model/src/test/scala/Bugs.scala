package ca.polymtl.inf8405.sevenwonders.model

import collection.MultiSet
import Resource._
import org.specs2.mutable._

class Bugs extends Specification {
  "#1" should {
    CumulativeProduction(MultiSet(Wood, Wood, Clay)) === CumulativeProduction(MultiSet(Clay, Wood, Wood))
  }
}
