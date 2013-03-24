package com.github.jedesah

object Utils {
  implicit class AugmentedList[A](value: List[A]) {

    def shiftRight: List[A] =
      if (value == Nil)
        Nil
      else
        value.last :: value.dropRight(1)

    def shiftLeft: List[A] =
      if (value == Nil)
        Nil
      else
        value.drop(1) :+ value.head
  }

  implicit class AugmentedTraversable[A](value: Traversable[A]) {
    def filterType[C]: Traversable[C] = value.filter(_.isInstanceOf[C]).map(_.asInstanceOf[C])

    def createMap[B](fun: A => B): Map[A, B] =
      value.map( elem => (elem, fun(elem))).toMap
  }

  implicit class AugmentedTuple3[A](value: (A, A, A)) {
    def toList = List(value._1, value._2, value._3)
    def min[B >: A](implicit cmp: Ordering[B]) = toList.min(cmp)
    def map[B](fun: A => B): (B, B, B) = (fun(value._1), fun(value._2), fun(value._3))
    def sum[B >: A](implicit num: Numeric[B]) = toList.sum(num)
  }
}

object Math {
  def pow(base: Int, exponent: Int): Int = scala.math.pow(base, exponent).toInt
}
