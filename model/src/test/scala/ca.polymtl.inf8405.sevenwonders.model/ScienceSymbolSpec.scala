package ca.polymtl.inf8405.sevenwonders.model

import org.specs2.mutable._

import Symbol._

class ScienceSymbolSpec extends Specification {
  "A SimpleScienceValue" should {
    "support adding another SimpleScienceValue" in {
      val a: ScienceSymbol = Compass
      (a + Tablet) === SimpleScienceSymbol(1, 0, 1)
      (Compass + Tablet) === SimpleScienceSymbol(1, 0, 1)
      (Tablet + Gear) === SimpleScienceSymbol(0, 1, 1)
    }

    "support adding an OptionalScienceValue" in {
      val actual = (Compass + (Tablet | Gear))
      val expected = ((Compass + Tablet) | (Compass + Gear))
      actual === expected
    }

    "support the | operator on a SimpleScienceValue" in {
      (Compass | Tablet) === OptionalScienceSymbol(Set(Compass, Tablet))
    }

    "support the | operator on a OptionalScienceValue" in {
      (Compass | (Tablet | Gear)) === OptionalScienceSymbol(Set(Compass, Tablet, Gear))
    }

    "have an Integer representing the victory point value" in {
      SimpleScienceSymbol(1,2,4).victoryPointValue === 28
      SimpleScienceSymbol(2,2,4).victoryPointValue === 38
      SimpleScienceSymbol(0,0,0).victoryPointValue === 0
      SimpleScienceSymbol(0,0,1).victoryPointValue === 1
    }

    "simplify expressions where possible" in {
      (Compass | Compass) === Compass
    }
  }

  "An OptionalScienceValue" should {
    "support adding a SimpleScienceValue" in {
      (Compass | Tablet) + Compass === ((Compass + Compass) | (Compass + Tablet))
    }

    "support adding another OptionalScienceValue" in {
      val actual = (Compass | Tablet) + (Compass | Gear)
      val expected = ((Compass + Compass) | (Compass + Gear) | (Tablet + Compass) | (Tablet + Gear))
      actual === expected
    }

    "support the | operaotr on a SimpleScienceValue" in {
      ((Compass | Tablet) | Gear) === OptionalScienceSymbol(Set(Compass, Tablet, Gear))
      ((Compass | Tablet) | Tablet) === OptionalScienceSymbol(Set(Compass, Tablet))
    }

    "support the | operator on an OptionalScienceValue" in {
      ((Compass | Tablet) | (Compass | Gear)) === (Compass | Tablet | Gear)
    }

    "have an Integer representing the victory point value" in {
      (SimpleScienceSymbol(1,2,4) + (Compass | Gear | Tablet)).victoryPointValue === 38
      (SimpleScienceSymbol(2,2,4) + (Compass | Gear | Tablet)).victoryPointValue === 47
      (SimpleScienceSymbol(0,0,1) + (Compass | Gear | Tablet)).victoryPointValue === 4
      (SimpleScienceSymbol(0,1,1) + (Compass | Gear | Tablet)).victoryPointValue === 10
    }
  }
}
