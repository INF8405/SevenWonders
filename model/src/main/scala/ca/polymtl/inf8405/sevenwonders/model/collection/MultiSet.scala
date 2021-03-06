package ca.polymtl.inf8405.sevenwonders.model
package collection

import scala.language.implicitConversions

import scala.util.Random
import utils.Utils._
import scala.collection.GenTraversableOnce

object conversions {
  implicit def setToMultiSet[A](from: Set[A]): MultiSet[A] =
    from.foldLeft(MultiSet[A]())((multiset, elem) => multiset + elem)
  implicit def unboxCircleNode[A](from: CircleNode[A]): A = from.toValue
}

trait MultiSet[A] extends Collection[A] {
  def map[B](mapFun: A => B): MultiSet[B]
  def toSet: Set[A]
  override def equals(other: Any) = {
    other match {
      case other: MultiSet[_] => if(isEmpty) other.isEmpty
                              else other.contains(head) && tail == other - head
      case _ => false
    }
  }
  override def --(other: GenTraversableOnce[Any]): MultiSet[A] = super.--(other).asInstanceOf[MultiSet[A]]
  override def --(other: Collection[Any]): MultiSet[A] = super.--(other).asInstanceOf[MultiSet[A]]
  def -(elem: Any): MultiSet[A]
  def tail: MultiSet[A]
  override def ++[A1 >: A](other: GenTraversableOnce[A1]): MultiSet[A1] = super.++(other).asInstanceOf[MultiSet[A1]]
  override def ++[A1 >: A](other: Collection[A1]): MultiSet[A1] = super.++(other).asInstanceOf[MultiSet[A1]]
  def +[A1 >: A](elem: A1): MultiSet[A1]
  override def takeRandom(nb: Int): MultiSet[A] = Random.shuffle(toList).take(nb).toMultiSet
  override def toString = "MultiSet(" + mkString(", ") + ")"
  def reduce(fun: (A, A) => A):A =
    if (isEmpty) throw new UnsupportedOperationException
    else if(size == 1) head else fun(head, tail.reduce(fun))
  def filter(pred: A => Boolean): MultiSet[A]
  def headOption: Option[A] = if (isEmpty) None else Some(head)
  def find(pred: A => Boolean):Option[A] = filter(pred).headOption
  def toCircle: Circle[A] = new Circle[A](toList: _*)
  def max[B >: A](implicit cmp: Ordering[B]): A =
    if (isEmpty) throw new UnsupportedOperationException
    else
    if (size == 1) head
    else if (cmp.compare(head, tail.max(cmp)) > 0) head else tail.max(cmp)
  def flatten[B](implicit asTraversable: (A) => GenTraversableOnce[B]): MultiSet[B] =
    foldLeft(MultiSet[B]())(_ ++ asTraversable(_))
}

object MultiSet {
  def apply[A](elements: A*): MultiSet[A] = DefaultMultiSet(elements : _*)
}

class DefaultMultiSet[A](private val content: Map[A, Int]) extends MultiSet[A] {
  def map[B](mapFun: A => B): DefaultMultiSet[B] =
    if (isEmpty) DefaultMultiSet()
    else tail.map(mapFun) + mapFun(head)
  def toSet: Set[A] = content.keySet
  def concat: String = if (isEmpty) "" else head.toString + tail.concat
  def head: A = content.head._1
  def tail: DefaultMultiSet[A] = {
    val newContent =
      if (content.head._2 > 1) content.updated(head, content(head) - 1)
      else content - head
    new DefaultMultiSet[A](newContent)
  }
  def filter(pred: A => Boolean): DefaultMultiSet[A] =
    foldLeft(DefaultMultiSet[A]())((multiset, elem) => if (pred(elem)) multiset + elem else multiset)
  def isEmpty: Boolean = content.isEmpty
  def +[A1 >: A](elem: A1): DefaultMultiSet[A1] = {
    val newContent: Map[A1, Int] =
      if (contains(elem)) content.updated(elem.asInstanceOf[A], content(elem.asInstanceOf[A]) + 1).asInstanceOf[Map[A1, Int]]
      else Map(elem -> 1) ++ content
    new DefaultMultiSet[A1](newContent)
  }
  def -(elem: Any): DefaultMultiSet[A] = {
    if (!contains(elem)) this
    else {
      val e = elem.asInstanceOf[A]
      if( e == null ) this
      else {
        val newContent =
          if (content(e) == 1) content - e
          else (content.updated(e, content(e) - 1))
        new DefaultMultiSet[A](newContent)
      }
    }
  }

  def contains(elem: Any) = {
    val e = elem.asInstanceOf[A]
    if( e != null ) {
      content.contains(e)
    } else false
  }
  override def hashCode = content.hashCode()
}

object DefaultMultiSet {
  def apply[A](elements: A*): DefaultMultiSet[A] = {
    val content =
      elements.foldLeft(Map[A,Int]()) {
      (map, elem) =>
        if (map.contains(elem)) map.updated(elem, map(elem) + 1)
        else map + (elem -> 1)
      }
    new DefaultMultiSet(content)
  }
}
