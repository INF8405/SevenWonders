import org.specs2.mutable._
import com.github.jedesah.SevenWonders._

class ScienceValueSpec extends Specification {
  "A SimpleScienceValue" should {
    "support adding another SimpleScienceValue" in {
      val a: ScienceValue = compass
      (a + tablet) === SimpleScienceValue(1, 0, 1)
      (compass + tablet) === SimpleScienceValue(1, 0, 1)
      (tablet + gear) === SimpleScienceValue(0, 1, 1)
    }

    "support adding an OptionalScienceValue" in {
      val actual = (compass + (tablet | gear))
      val expected = ((compass + tablet) | (compass + gear))
      actual === expected
    }

    "support the | operator on a SimpleScienceValue" in {
      (compass | tablet) === OptionalScienceValue(Set(compass, tablet))
    }

    "support the | operator on a OptionalScienceValue" in {
      (compass | (tablet | gear)) === OptionalScienceValue(Set(compass, tablet, gear))
    }

    "have an Integer representing the victory point value" in {
      SimpleScienceValue(1,2,4).victoryPointValue === 28
      SimpleScienceValue(2,2,4).victoryPointValue === 38
      SimpleScienceValue(0,0,0).victoryPointValue === 0
      SimpleScienceValue(0,0,1).victoryPointValue === 1
    }

    "simplify expressions where possible" in {
      (compass | compass) === compass
    }
  }

  "A OptionalScienceValue" should {
    "support adding a SimpleScienceValue" in {
      (compass | tablet) + compass === ((compass + compass) | (compass + tablet))
    }

    "support adding another OptionalScienceValue" in {
      val actual = (compass | tablet) + (compass | gear)
      val expected = ((compass + compass) | (compass + gear) | (tablet + compass) | (tablet + gear))
      actual === expected
    }

    "support the | operaotr on a SimpleScienceValue" in {
      ((compass | tablet) | gear) === OptionalScienceValue(Set(compass, tablet, gear))
      ((compass | tablet) | tablet) === OptionalScienceValue(Set(compass, tablet))
    }

    "support the | operator on an OptionalScienceValue" in {
      ((compass | tablet) | (compass | gear)) === (compass | tablet | gear)
    }

    "have an Integer representing the victory point value" in {
      (SimpleScienceValue(1,2,4) + (compass | gear | tablet)).victoryPointValue === 38
      (SimpleScienceValue(2,2,4) + (compass | gear | tablet)).victoryPointValue === 47
      (SimpleScienceValue(0,0,1) + (compass | gear | tablet)).victoryPointValue === 4
      (SimpleScienceValue(0,1,1) + (compass | gear | tablet)).victoryPointValue === 10
    }
  }
}
