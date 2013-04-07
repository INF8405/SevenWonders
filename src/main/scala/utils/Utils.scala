package utils

import scala.util.Random
import collection.MultiSet

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
    def allEqual: Boolean =
      if (value.isEmpty) true
      else value.tail.forall(_ == value.head)
    def remove(a: Any): List[A] = value.remove(a)
  }

  implicit class AugmentedTraversable[A](value: Traversable[A]) {
    def createMap[B](fun: A => B): Map[A, B] =
      value.map( elem => (elem, fun(elem))).toMap
    def toMultiSet: MultiSet[A] = value.foldLeft(MultiSet[A]())((multiset, elem) => multiset + elem)
    def hasDuplicate: Boolean =
      if (value.isEmpty) false
      else value.tail.exists(_ == value.head) || value.tail.hasDuplicate
    def remove(a: Any): Traversable[A] = value.filter(_ != a)
  }

  implicit class AugmentedSeq[A](value: Seq[A]) {
    def remove(a: Any): Seq[A] = value.remove(a)
  }

  implicit class AugmentedSet[A](value: Set[A]) {
    def takeRandom(nb: Int): Set[A] = Random.shuffle(value.to[List]).take(nb).to[Set]
  }

  implicit class AugmentedTupple2[A](value: (A, A)) {
    def +[B >: A](other: (A, A))(implicit num: Numeric[B]): (B , B) = (num.plus(value._1, other._1), num.plus(value._2, other._2))
    def sum[B >: A](implicit num: Numeric[B]): B = num.plus(value._1, value._2)
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
