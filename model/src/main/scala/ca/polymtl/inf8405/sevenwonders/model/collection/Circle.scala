package ca.polymtl.inf8405.sevenwonders.model
package collection

import utils.Utils._
import scala.util.Random
import scala.collection.GenIterable

class Circle[A](elems: A*) extends Traversable[A] {
  private val impl: List[CircleNode[A]] = elems.toList.map(elem => new CircleNode[A](elems.toList.shiftRight(elems.indexOf(elem)), elem, elems.toList.shiftLeft(elems.indexOf(elem))))
  def foreach[U](f: A => U): Unit =
    if (!elems.isEmpty) {
      f(elems.head)
      elems.tail.foreach(f)
    }
  def zip[B](that: GenIterable[B]): List[(A,B)] = {
    if( this.isEmpty || that.isEmpty ) List.empty[(A,B)]
    else ( this.head, that.head ) :: this.tail.zip( that.tail )
  }
  def map[B](fun: CircleNode[A] => B): Circle[B] = new Circle[B](impl.map(fun): _*)
  def createMap[B](fun: CircleNode[A] => B): Map[A, B] = map[(A,B)]{ elem: CircleNode[A] => (elem.toValue, fun(elem))}.toMap
  def replace(old: A, new_ : A): Circle[A] =
    new Circle(elems.map( elem => if (elem == old) new_ else elem): _*)
  def getLeft(of: A): A = impl.find(_.toValue == of).get.left
  def getRight(of: A): A = impl.find(_.toValue == of).get.right
  def getNeighbors(of: A): (A, A) = (getLeft(of), getRight(of))
  def remove(a: Any): Circle[A] = new Circle[A](elems.remove(a): _*)
  override def equals(other: Any) = other match {
    case other: Circle[_] => impl == other.impl
    case _ => false
  }
  override def filter(pred: A => Boolean): Circle[A] = new Circle[A](elems.filter(pred): _*)
  def shuffle: Circle[A] = new Circle[A](Random.shuffle(elems): _*)
  override def tail: Circle[A] = new Circle[A](elems.tail: _* )
}

class CircleNode[A](val left:A, val self: A, val right: A) {
  implicit def toValue: A = self
  override def equals(other: Any) = other match {
    case other: CircleNode[_] => left == other.left && self == other.self && right == other.right
    case _ => false
  }
}