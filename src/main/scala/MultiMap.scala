package com.github.jedesah

trait MultiMap[A, B] {
  def add(pair: (A,B)): MultiMap[A,B]
  def isEmpty: Boolean
  def head: (A,B)
  def tail: MultiMap[A,B]
  def values: MultiSet[B]
}

object MultiMap {
  def apply[A, B](pair: (A, B)*): MultiMap[A,B] = ???
}
