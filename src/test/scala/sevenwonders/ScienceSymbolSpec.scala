package sevenwonders

import org.specs2.mutable._
import com.github.jedesah.SevenWonders._

class ScienceSymbolSpec extends Specification {
  "A SimpleScienceValue" should {
    "support adding another SimpleScienceValue" in {
      val a: ScienceSymbol = compass
      (a + tablet) === SimpleScienceSymbol(1, 0, 1)
      (compass + tablet) === SimpleScienceSymbol(1, 0, 1)
      (tablet + gear) === SimpleScienceSymbol(0, 1, 1)
    }

    "support adding an OptionalScienceValue" in {
      val actual = (compass + (tablet | gear))
      val expected = ((compass + tablet) | (compass + gear))
      actual === expected
    }

    "support the | operator on a SimpleScienceValue" in {
      (compass | tablet) === OptionalScienceSymbol(Set(compass, tablet))
    }

    "support the | operator on a OptionalScienceValue" in {
      (compass | (tablet | gear)) === OptionalScienceSymbol(Set(compass, tablet, gear))
    }

    "have an Integer representing the victory point value" in {
      SimpleScienceSymbol(1,2,4).victoryPointValue === 28
      SimpleScienceSymbol(2,2,4).victoryPointValue === 38
      SimpleScienceSymbol(0,0,0).victoryPointValue === 0
      SimpleScienceSymbol(0,0,1).victoryPointValue === 1
    }

    "simplify expressions where possible" in {
      (compass | compass) === compass
    }
  }

  "An OptionalScienceValue" should {
    "support adding a SimpleScienceValue" in {
      (compass | tablet) + compass === ((compass + compass) | (compass + tablet))
    }

    "support adding another OptionalScienceValue" in {
      val actual = (compass | tablet) + (compass | gear)
      val expected = ((compass + compass) | (compass + gear) | (tablet + compass) | (tablet + gear))
      actual === expected
    }

    "support the | operaotr on a SimpleScienceValue" in {
      ((compass | tablet) | gear) === OptionalScienceSymbol(Set(compass, tablet, gear))
      ((compass | tablet) | tablet) === OptionalScienceSymbol(Set(compass, tablet))
    }

    "support the | operator on an OptionalScienceValue" in {
      ((compass | tablet) | (compass | gear)) === (compass | tablet | gear)
    }

    "have an Integer representing the victory point value" in {
      (SimpleScienceSymbol(1,2,4) + (compass | gear | tablet)).victoryPointValue === 38
      (SimpleScienceSymbol(2,2,4) + (compass | gear | tablet)).victoryPointValue === 47
      (SimpleScienceSymbol(0,0,1) + (compass | gear | tablet)).victoryPointValue === 4
      (SimpleScienceSymbol(0,1,1) + (compass | gear | tablet)).victoryPointValue === 10
    }
  }
}
