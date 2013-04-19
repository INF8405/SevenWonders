package ca.polymtl.inf8405.sevenwonders

import model.collection.MultiMap

package object model {
  type Trade = MultiMap[Resource, NeighborReference]
  type Age = Int
  type PlayerAmount = Int

  val emptyTrade = MultiMap[Resource, NeighborReference]()
}
