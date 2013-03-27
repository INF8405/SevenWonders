package ca.polymtl.inf8405.sevenwonders
package app

import ApiHelper._

import collection.mutable.{ Set => MSet }

trait Game {

  def join( player: GameClient )
  def disconnect( player: GameClient )
  def start()

  def playCard( card: Card, trade: Trade )
  def playWonder( trade: Trade )
  def discard( card: Card )
}

class GameImpl extends Game {

  def join( player: GameClient ){
    players.add( player )
  }

  def disconnect( player: GameClient ){
    players.remove( player )

  }

  def start(){

  }

  def playCard(card: Card, trade: Trade ) {}
  def playWonder( trade: Trade ) {}
  def discard(card: Card) {}

  def broadcast( f: GameClient => Unit ) {
    players.foreach( f(_) )
  }


  private val players = MSet.empty[GameClient]
}
