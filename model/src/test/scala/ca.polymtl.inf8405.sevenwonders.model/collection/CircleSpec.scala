package ca.polymtl.inf8405.sevenwonders.model
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
    "tail" in {
      val test = new Circle( 1, 2, 3 )
      test.tail === new Circle( 2, 3 )
    }
    "zip" in {
      val testA = new Circle( 1.1, 2.2, 3.3 )
      val testB = scala.collection.mutable.Set( 1, 2, 3 )

      testA.zip( testB ) === List( ( 1.1, 1 ), ( 2.2, 2 ), ( 3.3, 3 )  )
    }
    "order" in {
      val test = new Circle(1,2,3)

      test.getLeft(2) ==== 1
      test.getRight(2) ==== 3
      test.getLeft(1) ==== 3
      test.getRight(1) ==== 2
      test.getLeft(3) ==== 2
      test.getRight(3) ==== 1
    }

    "toList" in {
      val test = new Circle(1,2,3)

      test.toList ==== List(1,2,3)


    }

    "random" in {
      // players
      val test = new Circle('bab,'ephesos,'hali)

      val ( before, me :: after ) = test.toList.span( _ != 'bab )
      val players = me :: before.reverse ::: after.reverse

      players ==== List('bab,'hali,'ephesos)

      val ( before2, me2 :: after2 ) = test.toList.span( _ != 'ephesos )
      val players2 = me2 :: before2.reverse ::: after2.reverse

      players2 ==== List('ephesos,'bab,'hali)

      val ( before3, me3 :: after3 ) = test.toList.span( _ != 'hali )
      val players3 = me3 :: before3.reverse ::: after3.reverse

      players3 ==== List('hali,'ephesos,'bab)
    }
  }
}
