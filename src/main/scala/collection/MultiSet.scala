package collection

trait MultiSet[A] extends Collection[A] {
  def toSet: Set[A]
  override def equals(other: Any) = {
    other match {
      case other: MultiSet[_] => if(isEmpty) other.isEmpty
                              else other.contains(head) && tail == other - head
      case _ => false
    }
  }
}

object MultiSet {
  def apply[A](elements: A*) = DefaultMultiSet(elements : _*)
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
  def isEmpty: Boolean = content.isEmpty
  def +(elem: A): DefaultMultiSet[A] = {
    val newContent =
      if (contains(elem)) content.updated(elem, content(elem) + 1)
      else content + (elem -> 1)
    new DefaultMultiSet[A](newContent)
  }
  def -(elem: Any): DefaultMultiSet[A] = {
    elem match {
      case elem: A => {
        if (!contains(elem)) this
        else {
          val newContent =
            if (content(elem) == 1) content - elem
            else (content.updated(elem, content(elem) - 1))
          new DefaultMultiSet[A](newContent)
        }
      }
      case _ => this
    }
  }
  def contains(elem: Any) = {
    elem match {
      case elem: A => content.contains(elem)
      case _ => false
    }
  }
  override def toString = "MultiSet(" + this.mkString(", ") + ")"
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
