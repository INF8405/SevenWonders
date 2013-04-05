package ca.polymtl.inf8405.sevenwonders
package test

import api.SevenWondersApi.AsyncClient

import org.apache.thrift.async.AsyncMethodCallback

package object client {

  type User = String
  type Category = String
  type Card = String
  type GameId = String

  type JList[T] = java.util.List[T]
  type JMap[K,V] = java.util.Map[K,V]

  def SERVER = null
  def CLIENT = null
}
