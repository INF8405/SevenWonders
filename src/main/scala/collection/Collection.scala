package collection

import scala.collection.GenTraversableOnce

trait Collection[+A] {
  def toList: List[A] = if (isEmpty) Nil else head :: tail.toList
  def map[B](mapFun: A => B): Collection[B]
  def mkString(sep: String): String = this.map(_.toString + sep).concat.dropRight(sep.size)
  def concat: String
  def head: A
  def tail: Collection[A]
  def isEmpty: Boolean
  def ++[A1 >: A](other: GenTraversableOnce[A1]): Collection[A1] =
    other.foldLeft[Collection[A1]](this)((newColl, otherElem) => newColl + otherElem)
  def ++[A1 >: A](other: Collection[A1]): Collection[A1] =
    other.foldLeft[Collection[A1]](this)((newColl, otherElem) => newColl + otherElem)
  def +[A1 >: A](elem: A1): Collection[A1]
  def -(elem: Any): Collection[A]
  def --(other: GenTraversableOnce[Any]): Collection[A] =
    other.foldLeft(this)((newColl, otherElem) => newColl - otherElem)
  def --(other: Collection[Any]): Collection[A] =
    other.foldLeft(this)((newColl, otherElem) => newColl - otherElem)
  def foldLeft[B](seed: B)(fun: (B, A) => B): B = {
    if (isEmpty) seed
    else tail.foldLeft(fun(seed, head))(fun)
  }
  def contains(elem: Any): Boolean
  def sum[B >: A](implicit num: Numeric[B]): B = foldLeft(num.zero)(num.plus)
  def size: Int = if (isEmpty) 0 else 1 + tail.size
  def takeRandom(nb: Int): Collection[A]
  def count(pred: A => Boolean): Int =
    if (isEmpty) 0
    else (if (pred(head)) 1 else 0) + tail.count(pred)
}