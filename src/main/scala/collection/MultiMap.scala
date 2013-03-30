package collection

trait MultiMap[A, B] /*extends Collection[(A, B)]*/ {
  def isEmpty: Boolean
  def head: (A,B)
  def tail: MultiMap[A,B]
  def values: MultiSet[B]
  def contains(key: A): Boolean
  def +(elem: (A, B)): MultiMap[A, B]
}

object MultiMap {
  def apply[A, B](pairs: (A, B)*): MultiMap[A,B] = DefaultMultiMap(pairs : _*)
}

class DefaultMultiMap[A, B](private val impl: Map[A, MultiSet[B]]) extends MultiMap[A, B] {
  def values: MultiSet[B] = if (isEmpty) MultiSet() else tail.values + head._2
  def takeRandom(nb: Int): MultiSet[(A,B)] = ???
  def +(elem: (A, B)): DefaultMultiMap[A,B] = {
    val newImpl: Map[A, MultiSet[B]] = if (contains(elem._1)) impl.updated(elem._1, impl(elem._1) + elem._2)
                  else impl + (elem._1 -> MultiSet(elem._2))
    new DefaultMultiMap[A, B](newImpl)
  }
  def tail: DefaultMultiMap[A, B] = {
    val newImpl: Map[A, MultiSet[B]] = if (impl(head._1).size > 1) impl.updated(head._1, impl(head._1).tail)
                                       else impl - head._1
    new DefaultMultiMap[A, B](newImpl)
  }
  def isEmpty: Boolean = impl.isEmpty
  def head: (A, B) = (impl.head._1, impl.head._2.head)
  def contains(key: A): Boolean = impl.contains(key)
  override def toString = impl.toString
  override def equals(other: Any) = other match {
    case other: DefaultMultiMap[A, B] => other.impl == impl
    case _ => false
  }
}

object DefaultMultiMap {
  def apply[A, B](pairs: (A, B)*): DefaultMultiMap[A, B] = {
    val impl: Map[A, MultiSet[B]] = pairs.foldLeft(Map[A, MultiSet[B]]())((map, elem) =>
      if (map.contains(elem._1)) map.updated(elem._1, map(elem._1))
      else map + (elem._1 -> MultiSet[B](elem._2))
    )
    new DefaultMultiMap[A, B](impl)
  }
}