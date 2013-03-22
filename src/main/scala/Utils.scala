package com.github.jedesah

object Utils {
  implicit class AugmentedList[A](value: List[A]) {

    def shiftRight: List[A] =
      if (value == Nil)
        Nil
      else
        value.last :: value.drop(1)

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
}
