package ca.polymtl.inf8405.sevenwonders.model

import collection.MultiSet

trait Action {
  def perform(current:Game, by: Player): GameDelta
}
case class Build(card: Card, trade: Trade, wonder: Boolean) extends Action {
  def perform(current:Game, by: Player) = {
    val (newPlayer, coinsToGive) = by.build(card, trade, wonder)
    val left = current.players.getLeft(by)
    val right = current.players.getRight(by)
    val leftDelta = PlayerDelta(coinDelta = coinsToGive._1)
    val rightDelta = PlayerDelta(coinDelta = coinsToGive._2)
    GameDelta(Map(by -> newPlayer.-(by), left -> leftDelta, right -> rightDelta))
  }
}
case class Discard(card: Card) extends Action {
  def perform(current:Game, by: Player) =
    GameDelta(Map(by -> by.discard(card).-(by)), MultiSet(card))
}
case class BuildForFree(card: Card) extends Action {
  def perform(current:Game, by: Player) =
    GameDelta(Map(by -> by.buildForFree(card).-(by)))
}