package collection

trait Collection[A] {
  def map[B](mapFun: A => B): Collection[B]
  def mkString(sep: String): String = this.map(_.toString + sep).concat.dropRight(sep.size)
  def concat: String
  def head: A
  def tail: Collection[A]
  def isEmpty: Boolean
  def ++(other: Collection[A]): Collection[A] =
    other.foldLeft(this)((newColl, otherElem) => newColl + otherElem)
  def +(elem: A): Collection[A]
  def -(elem: Any): Collection[A]
  def --(other: Collection[Any]): Collection[A] =
    other.foldLeft(this)((newColl, otherElem) => newColl - otherElem)
  def foldLeft[B](seed: B)(fun: (B, A) => B): B = {
    if (isEmpty) seed
    else tail.foldLeft(fun(seed, head))(fun)
  }
  def contains(elem: Any): Boolean
}