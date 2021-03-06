package ca.polymtl.inf8405.sevenwonders.model

import collection.{MultiMap, MultiSet}

trait Action {
  def perform(current:Game, by: Player): GameDelta
}
case class Build(card: Card, trade: Trade = MultiMap(), wonder: Boolean = false) extends Action {
  def perform(current:Game, by: Player) = {
    val (newPlayer, (coinsLeft, coinsRight)) = by.build(card, trade, wonder)
    val left = current.players.getLeft(by)
    val right = current.players.getRight(by)
    val leftDelta = PlayerDelta(coinDelta = coinsLeft)
    val rightDelta = PlayerDelta(coinDelta = coinsRight)
    GameDelta(Map(by -> ( newPlayer - by ), left -> leftDelta, right -> rightDelta))
  }
}
case class Discard(card: Card) extends Action {
  def perform(current:Game, by: Player) =
    GameDelta(Map(by -> (by.discard(card) - by)), MultiSet(card))
}
case class BuildForFree(card: Card) extends Action {
  def perform(current:Game, by: Player) =
    GameDelta(Map(by -> (by.buildForFree(card) - by)))
}