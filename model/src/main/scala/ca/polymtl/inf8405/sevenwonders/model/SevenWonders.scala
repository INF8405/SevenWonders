package ca.polymtl.inf8405.sevenwonders.model

import scala.language.implicitConversions

import util.Random
import utils.Utils
import Utils._

import collection.MultiSet
import collection.Circle

import CardCollection._
import CivilizationCollection._

object SevenWonders 
{
  def beginGame( nbPlayers: Int , playWithCities: Boolean = false): Game = {
    val cards =
      if (playWithCities) citySevenWonders.generateCards(nbPlayers)
      else classicSevenWonders.generateCards(nbPlayers)

    val qualifiedCivilizations =
      if (playWithCities) baseCivilizations ++ cityCivilizations
      else baseCivilizations

    val chosenCivilizations = Random.shuffle(qualifiedCivilizations.toList).take(nbPlayers)
    val players = chosenCivilizations.map{ civ =>
      Player(civ.takeRandom, MultiSet(), 3)
    }
    Game(new Circle[Player](players: _*), cards, MultiSet()).beginAge()
  }

  class ClassicGameSetup(allCards: Map[Age, Map[PlayerAmount, MultiSet[Card]]], guildCards: Set[GuildCard]) {

    import collection.conversions._

    def generateCards(nbPlayers: Int): Map[Age, MultiSet[Card]] = {
      if (nbPlayers < 3) throw new IllegalArgumentException("You cannot currently build less than three players")
      else {
        // Adding all cards that should be used depending on the amount of players
        val cardsWithoutGuilds =
          allCards.mapValues( cards => (3 to nbPlayers).foldLeft(MultiSet[Card]())((set, key) => set ++ cards(key)))
        // Add 2 + nbPlayers guild cards selected randomly
        cardsWithoutGuilds.updated(3, cardsWithoutGuilds(3).++[Card](setToMultiSet(AugmentedSet(guildCards).takeRandom(nbPlayers + 2))))
      }
    }
  }

  class CityGameSetup(allCards: Map[Age, Map[PlayerAmount, MultiSet[Card]]], guildCards: Set[GuildCard], cityCards: Map[Age, Set[CityCard]])
    extends ClassicGameSetup(allCards, guildCards) {
    override def generateCards(nbPlayers: Int): Map[Age, MultiSet[Card]] = {
      val classicSetup = super.generateCards(nbPlayers)
      classicSetup.map {
        case (age, cards) =>
          (age, cards ++ AugmentedSet(cityCards(age)).takeRandom(nbPlayers))
      }
    }
  }

  // Game Setup
  val classicSevenWonders = new ClassicGameSetup(normalBaseCards, baseGuilds)
  val citySevenWonders = new CityGameSetup(normalBaseCards, baseGuilds ++ cityGuilds, cityCards)
}