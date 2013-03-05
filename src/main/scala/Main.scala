package com.github.jedesah

import com.sidewayscoding.Multiset
import util.Random

object SevenWonders 
{
  def beginGame( nbPlayers: Int ): Game = ???

  class Card( 
    name: String, 
    cost: Multiset[Resource],
    evolutions: Set[Card]
  )

  sealed trait ScienceCategory
  object ScienceCompass extends ScienceCategory
  object ScienceGear extends ScienceCategory
  object ScienceStone extends ScienceCategory

  case class ScienceCard(
    name: String,
    cost: Multiset[Resource],
    evolutions: Set[Card],
    category: ScienceCategory
  ) extends Card( name, cost, evolutions )

  case class MilitaryCard(
    name: String,
    cost: Multiset[Resource],
    evolutions: Set[Card],
    value: Int
  ) extends Card( name, cost, evolutions )

  class CommercialCard(
    name: String,
    cost: Multiset[Resource],
    evolutions: Set[Card]
  ) extends Card( name, cost, evolutions )

  case class RebateCommercialCard(
    name: String,
    cost: Multiset[Resource],
    evolutions: Set[Card],
    affectedResources: Set[Resource],
    fromWho: Set[NeighboorReference]
  ) extends CommercialCard( name, cost, evolutions )

  case class ProductionCommercialCard(
    name: String,
    cost: Multiset[Resource],
    evolutions: Set[Card],
    prod: Production
  ) extends CommercialCard( name, cost, evolutions )

  case class RewardCommercialCard(
    name: String, 
    cost: Multiset[Resource],
    evolutions: Set[Card],
    coinReward: Option[CoinReward],
    victoryReward: Option[VictoryPointReward]
  ) extends CommercialCard( name, cost, evolutions )

  class ResourceCard(
    name: String,
    cost: Multiset[Resource],
    production: Production
  ) extends Card(name, cost, Set() )

  case class RawMaterialCard(
    name: String,
    cost: Multiset[Resource],
    production: Production
  ) extends ResourceCard(name, cost, production) {
    def this(name: String, production: Production) = this(name, Multiset(), production)
  }

  case class ManufacturedGoodCard(
    name: String,
    cost: Multiset[Resource],
    production: Production
  ) extends ResourceCard(name, cost, production) {
    def this(name: String, production: Production) = this(name, Multiset(), production)
  }
  
  case class CivilianCard(
    name: String,
    cost: Multiset[Resource],
    evolutions: Set[Card],
    amount: Int
  ) extends Card( name, cost, evolutions )

  class GuildCard(name: String, cost: Multiset[Resource]) extends Card(name, cost, Set())

  case class VictoryPointsGuildCard(
    name: String,
    cost: Multiset[Resource],
    vicPointReward: VictoryPointReward
  ) extends GuildCard(name, cost)

  trait CoinReward
  case class SimpleCoinReward(amount: Int) extends CoinReward
  case class ComplexCoinReward(
    amount: Int,
    //forEach: Manifest[Card],
    from: Set[PlayerReference]
  ) extends CoinReward

  case class VictoryPointReward(
    amount: Int,
    //forEach: ClassTag[Card],
    from: Set[PlayerReference]
  )

  sealed trait Resource
  sealed trait RawMaterial extends Resource
  sealed trait ManufacturedGood extends Resource
  object Clay extends RawMaterial
  object Wood extends RawMaterial
  object Ore extends RawMaterial
  object Stone extends RawMaterial
  object Glass extends ManufacturedGood
  object Paper extends ManufacturedGood
  object Tapestry extends ManufacturedGood

  sealed trait PlayerReference
  sealed trait NeighboorReference extends PlayerReference
  object Left extends NeighboorReference
  object Right extends NeighboorReference
  object Self extends PlayerReference

  trait Production {
    def consume(resources: Multiset[Resource]): Set[Multiset[Resource]]
    def +(other: Production): Production
    def |(other: Production): Production
  }
  case class OptionalProduction(possibilities: Set[CumulativeProduction]) extends Production {
    def consume(resources: Multiset[Resource]): Set[Multiset[Resource]] = possibilities.map(_.consume(resources).head)
    def +(other: Production): Production = other match {
      case OptionalProduction(otherPossibilities) => OptionalProduction(possibilities.map( poss1 => otherPossibilities.map( poss2 => poss1 + poss2)).flatten)
      case cumProd: CumulativeProduction => OptionalProduction(possibilities.map(_ + cumProd))
    }
    def |(other: Production): Production = other match {
      case OptionalProduction(otherPossibilities) => OptionalProduction(possibilities ++ otherPossibilities)
      case cumProd: CumulativeProduction => OptionalProduction(possibilities + cumProd)
    }
  }
  case class CumulativeProduction(resources: Multiset[Resource]) extends Production {
    def this(resource: Resource) = this(Multiset(resource))
    def consume(resources: Multiset[Resource]): Set[Multiset[Resource]] = Set(this.resources.diff(resources))
    def +(other: Production) = throw new Error
    def +(other: OptionalProduction) = other + this
    def +(other: CumulativeProduction) = CumulativeProduction(resources ++ other.resources)
    def |(other: Production) = throw new Error
    def |(other: OptionalProduction) = other | this
    def |(other: CumulativeProduction) = OptionalProduction(Set(this, other))
  }

  implicit def ResourceToProduction(value: Resource) = new CumulativeProduction(value)

  case class Player(hand: Set[Card], coins: Int, battleMarkers: Multiset[BattleMarker], played: Set[Card], civilization: Civilization) {
    def discard(card: Card): Player = ???
    def play(card: Card, tradedResources: Map[Resource, Multiset[NeighboorReference]]): Player = ???
    def playableCards(availableThroughTrade: Map[NeighboorReference, Production]): Set[Card] = ???
    def totalProduction: Production = ???
    def tradableProduction: Production = ???
    def score(neightboorCards: Map[NeighboorReference, Multiset[Card]]): Int = ???
  }

  type Age = Int

  case class Game(players: List[Player], cards: Map[Age, Multiset[Card]], discarded: Multiset[Card]) {
    def getNeighboors(player: Player): Set[Player] = ???
    def getLeftNeighboor(player: Player): Player = ???
    def getRightNeighboor(player: Player): Player = ???
    def playTurn(actions: Map[Player, Action]): Game = ???
    def currentAge = cards.keys.toList.reverse.find(cards(_).isEmpty).get
  }

  class Action(card: Card)
  case class PlayAction(card: Card, consume: Map[Resource, Multiset[NeighboorReference]]) extends Action(card)
  case class DiscardAction(card: Card) extends Action(card)

  sealed trait BattleMarker
  class DefeatBattleMarker extends BattleMarker
  case class VictoryBattleMarker(vicPoints: Int) extends BattleMarker

  type PlayerAmount = Int

  case class GameSetup(allCards: Map[Age, Map[PlayerAmount, Multiset[Card]]], guildCards: Set[GuildCard]) {
    def generateCards(nbPlayers: Int): Map[Age, Multiset[Card]] = {
      if (nbPlayers < 3) throw new IllegalArgumentException("You cannot currently play less than three players")
      else {
        // Adding all cards that should be used depending on the amount of players
        val cardsWithoutGuilds =
          allCards.mapValues( cards => (3 to nbPlayers).foldLeft(Multiset.empty[Card])((set, key) => set ++ cards(key)))
        // Add 2 + nbPlayers guild cards selected randomly
        val shuffledGuildCads = Random.shuffle(guildCards.toList)
        cardsWithoutGuilds.updated(3, cardsWithoutGuilds(3) ++ shuffledGuildCads.take(nbPlayers + 2))
      }
    }
  }

  case class Civilization(name: String, base:Production)

  ////
  // AGE I
  ////

  // Commercial Cards
  val TAVERN = RewardCommercialCard("TAVERN", Multiset(), Set(), Some(SimpleCoinReward(5)), None)
  val WEST_TRADING_POST = RebateCommercialCard("WEST TRADING POST", Multiset(), Set(FORUM), Set(Clay, Stone, Wood, Ore), Set(Left))
  val MARKETPLACE = RebateCommercialCard("MARKETPLACE", Multiset(), Set(CARAVANSERY), Set(Glass, Tapestry, Paper), Set(Left, Right))
  val EAST_TRADING_POST = RebateCommercialCard("EAST TRADING POST", Multiset(), Set(FORUM), Set(Clay, Stone, Wood, Ore), Set(Right))

  // Military Cards
  val STOCKADE = MilitaryCard("STOCKADE", Multiset(Wood), Set(), 1)
  val BARRACKS = MilitaryCard("BARRACKS", Multiset(Ore), Set(), 1)
  val GUARD_TOWER = MilitaryCard("GUARD TOWER", Multiset(Clay), Set(), 1)

  // Science Cards
  val WORKSHOP = ScienceCard("WORKSHOP", Multiset(Glass), Set(LABORATORY, ARCHERY_RANGE), ScienceGear)
  val SCRIPTORIUM = ScienceCard("SCRIPTORIUM", Multiset(Paper), Set(COURTHOUSE, LIBRARY), ScienceStone)
  val APOTHECARY = ScienceCard("APOTHECARY", Multiset(Tapestry), Set(STABLES, DISPENSARY), ScienceCompass)


  // Civilian Cards
  val THEATER = CivilianCard("THEATER", Multiset(), Set(STATUE), 2)
  val BATHS = CivilianCard("BATHS", Multiset(Stone), Set(AQUEDUCT), 3)
  val ALTAR = CivilianCard("ALTAR", Multiset(), Set(TEMPLE), 2)
  val PAWNSHOP = CivilianCard("PAWNSHOP", Multiset(), Set(), 3)

  // TODO: Add coin cost
  // Raw Material Cards
  val TREE_FARM = new RawMaterialCard("TREE FARM", Wood | Clay)
  val MINE = new RawMaterialCard("MINE", Stone | Ore)
  val CLAY_PIT = new RawMaterialCard("CLAY PIT",Clay | Ore)
  val TIMBER_YARD = new RawMaterialCard("TIMBER YARD", Stone | Wood)
  val STONE_PIT = new RawMaterialCard("STONE PIT", Stone)
  val FOREST_CAVE = new RawMaterialCard("FOREST CAVE", Wood | Ore)
  val LUMBER_YARD = new RawMaterialCard("LUMBER YARD", Wood)
  val ORE_VEIN = new RawMaterialCard("ORE VEIN", Ore)
  val EXCAVATION = new RawMaterialCard("EXCAVATION", Stone | Clay)
  val CLAY_POOL = new RawMaterialCard("CLAY POOL", Clay)

  // Manufactured Good Cards
  val LOOM = new ManufacturedGoodCard("LOOM", Tapestry)
  val GLASSWORKS = new ManufacturedGoodCard("GLASSWORKS", Glass)
  val PRESS = new ManufacturedGoodCard("PRESS", Paper)

  ////
  // AGE II
  ////

  // Commercial Cards
  val CARAVANSERY = ProductionCommercialCard("CARAVANSERY", Multiset(Wood, Wood), Set(LIGHTHOUSE), Wood | Stone | Ore | Clay)
  val FORUM = ProductionCommercialCard("FORUM", Multiset(Clay, Clay), Set(HAVEN), Glass | Tapestry | Paper)
  val BAZAR = RewardCommercialCard("BAZAR", Multiset(), Set(), Some(ComplexCoinReward(2, Set(Left, Self, Right))), None)
  val VINEYARD = RewardCommercialCard("VINEYARD", Multiset(), Set(), Some(ComplexCoinReward(1, Set(Left, Self, Right))), None)

  // Military Cards
  val WALLS = MilitaryCard("WALLS", Multiset(Stone, Stone, Stone), Set(FORTIFICATIONS), 2)
  val ARCHERY_RANGE = MilitaryCard("ARCHERY RANGE", Multiset(Wood, Wood, Ore), Set(), 2)
  val TRAINING_GROUND = MilitaryCard("TRAINING GROUND", Multiset(Ore, Ore, Wood), Set(CIRCUS), 2)
  val STABLES = MilitaryCard("STABLES", Multiset(Clay, Wood, Ore), Set(), 2)

  // Science Cards
  val SCHOOL = ScienceCard("SCHOOL", Multiset(Wood, Paper), Set(ACADEMY, STUDY), ScienceStone)
  val LIBRARY = ScienceCard("LIBRARY", Multiset(Stone, Stone, Tapestry), Set(SENATE, UNIVERSITY), ScienceStone)
  val LABORATORY = ScienceCard("LABORATORY", Multiset(Clay, Clay, Paper), Set(OBSERVATORY, SIEGE_WORKSHOP), ScienceGear)
  val DISPENSARY = ScienceCard("DISPENSARY", Multiset(Ore, Ore, Glass), Set(LODGE, ARENA), ScienceCompass)

  // Civilian Cards
  val AQUEDUCT = CivilianCard("AQUEDUC", Multiset(Stone, Stone, Stone), Set(), 5)
  val STATUE = CivilianCard("STATUE", Multiset(Ore, Ore, Wood), Set(GARDENS), 4)
  val TEMPLE = CivilianCard("TEMPLE", Multiset(Wood, Clay, Glass), Set(PANTHEON), 3)
  val COURTHOUSE = CivilianCard("COURTHOUSE", Multiset(Clay, Clay, Tapestry), Set(), 4)

  // TODO: Add coin cost
  // Raw Material Cards
  val FOUNDRY = new RawMaterialCard("FOUNDRY", Ore + Ore)
  val QUARRY = new RawMaterialCard("QUARRY", Stone + Stone)
  val BRICKYARD = new RawMaterialCard("BRICKYARD", Clay + Clay)
  val SAWMILL = new RawMaterialCard("SAWMILL", Wood + Wood)

  ////
  // AGE III
  ////

  // Commercial Cards
  val ARENA = RewardCommercialCard("ARENA", Multiset(Stone, Stone, Ore), Set(), Some(ComplexCoinReward(3, Set(Self))), Some(VictoryPointReward(1, Set(Self))))
  val CHAMBER_OF_COMMERCE = RewardCommercialCard("CHAMBER OF COMMERCE", Multiset(Clay, Clay, Paper), Set(), Some(ComplexCoinReward(2, Set(Self))), Some(VictoryPointReward(2, Set(Self))))
  val LIGHTHOUSE = RewardCommercialCard("LIGHTHOUSE", Multiset(Stone, Glass), Set(), Some(ComplexCoinReward(1, Set(Self))), Some(VictoryPointReward(1, Set(Self))))
  val HAVEN = RewardCommercialCard("HAVEN", Multiset(Wood, Ore, Tapestry), Set(), Some(ComplexCoinReward(1, Set(Self))), Some(VictoryPointReward(1, Set(Self))))

  // Military Cards
  val CIRCUS = MilitaryCard("CIRCUS", Multiset(Stone, Stone, Stone, Ore), Set(), 3)
  val FORTIFICATIONS = MilitaryCard("FORTIFICATIONS", Multiset(Ore, Ore, Ore, Stone), Set(), 3)
  val ARSENAL = MilitaryCard("ARSENAL", Multiset(Wood, Wood, Ore, Tapestry), Set(), 3)
  val SIEGE_WORKSHOP = MilitaryCard("SIEGE WORKSHOP", Multiset(Clay, Clay, Clay, Wood), Set(), 3)

  // Science Cards
  val OBSERVATORY = ScienceCard("OBSERVATORY", Multiset(Ore, Ore, Glass, Tapestry), Set(), ScienceGear)
  val ACADEMY = ScienceCard("ACADEMY", Multiset(Stone, Stone, Stone), Set(), ScienceCompass)
  val LODGE = ScienceCard("LODGE", Multiset(Clay, Clay, Paper, Tapestry), Set(), ScienceCompass)
  val UNIVERSITY = ScienceCard("UNIVERSITY", Multiset(Wood, Wood, Paper, Glass), Set(), ScienceStone)
  val STUDY = ScienceCard("STUDY", Multiset(Wood, Paper, Tapestry), Set(), ScienceGear)

  // Civilian Cards
  val TOWN_HALL = CivilianCard("TOWN HALL", Multiset(Stone, Stone, Ore, Glass), Set(), 6)
  val PALACE = CivilianCard("PALACE", Multiset(Stone, Ore, Wood, Clay, Glass, Paper, Tapestry), Set(), 8)
  val PANTHEON = CivilianCard("PANTHEON", Multiset(Clay, Clay, Ore, Glass, Paper, Tapestry), Set(), 7)
  val GARDENS = CivilianCard("GARDENS", Multiset(Clay, Clay, Wood), Set(), 5)
  val SENATE = CivilianCard("SENATE", Multiset(Wood, Wood, Stone, Ore), Set(), 6)

  // Guilds
  val STRATEGISTS_GUILD = VictoryPointsGuildCard("STARTEGISTS GUILD", Multiset(Ore, Ore, Stone, Tapestry), VictoryPointReward(1, Set(Left, Right)))
  val TRADERS_GUILD = VictoryPointsGuildCard("TRADERS GUILD", Multiset(Glass, Tapestry, Paper), VictoryPointReward(1, Set(Left, Right)))
  val MAGISTRATES_GUILD = VictoryPointsGuildCard("MAGISTRATES GUILD", Multiset(Wood, Wood, Wood, Stone, Tapestry), VictoryPointReward(1, Set(Left, Right)))
  val SHOPOWNERS_GUILD = VictoryPointsGuildCard("SHOPOWNERS GUILD", Multiset(Wood, Wood, Wood, Glass, Paper), VictoryPointReward(1, Set(Self)))
  val CRAFTMENS_GUILD = VictoryPointsGuildCard("CRAFTSMENS GUILD", Multiset(Ore, Ore, Stone, Stone), VictoryPointReward(2, Set(Left, Right)))
  val WORKERS_GUILD = VictoryPointsGuildCard("WORKERS GUILD", Multiset(Ore, Ore, Clay, Stone, Wood), VictoryPointReward(1, Set(Left, Right)))
  val PHILOSOPHERS_GUILD = VictoryPointsGuildCard("PHILOSOPHERS GUILD", Multiset(Clay, Clay, Clay, Paper, Tapestry), VictoryPointReward(1, Set(Left, Right)))
  object SCIENTISTS_GUILD extends GuildCard("SCIENTISTS GUILD", Multiset(Wood, Wood, Ore, Ore, Paper))
  val SPIES_GUILD = VictoryPointsGuildCard("SPIES GUILD", Multiset(Clay, Clay, Clay, Glass), VictoryPointReward(1, Set(Left, Right)))
  val BUILDERS_GUILD = VictoryPointsGuildCard("BUILDERS GUILD", Multiset(Stone, Stone, Clay, Clay, Glass), VictoryPointReward(1, Set(Left, Self, Right)))

  // Civilizations
  val RHODOS = Civilization("RHODOS", Ore)
  val ALEXANDRIA = Civilization("ALEXANDRIA", Glass)
  val HALIKARNASSOS = Civilization("HALIKARNASSOS", Tapestry)
  val OLYMPIA = Civilization("OLYMPIA", Wood)
  val GIZAH = Civilization("GIZAH", Stone)
  val EPHESOS = Civilization("EPHESOS", Paper)
  val BABYLON = Civilization("BABYLON", Clay)

  // Game Setup
  val classicSevenWonders = GameSetup(
    Map(
      1 -> Map(
        3 -> Multiset(APOTHECARY, CLAY_POOL, ORE_VEIN, WORKSHOP, SCRIPTORIUM, BARRACKS, EAST_TRADING_POST, STOCKADE, CLAY_PIT, LOOM, GLASSWORKS, THEATER, BATHS, TIMBER_YARD, PRESS, STONE_PIT, MARKETPLACE, GUARD_TOWER, WEST_TRADING_POST, ALTAR, LUMBER_YARD),
        4 -> Multiset(GUARD_TOWER, LUMBER_YARD, PAWNSHOP, TAVERN, SCRIPTORIUM, EXCAVATION, ORE_VEIN),
        5 -> Multiset(CLAY_POOL, ALTAR, APOTHECARY, BARRACKS, STONE_PIT, TAVERN, FOREST_CAVE),
        6 -> Multiset(THEATER, PRESS, GLASSWORKS, LOOM, MARKETPLACE, MINE, TREE_FARM),
        7 -> Multiset(WORKSHOP, EAST_TRADING_POST, STOCKADE, BATHS, WEST_TRADING_POST, TAVERN, PAWNSHOP)
      ),
      2 -> Map(
        3 -> Multiset(CARAVANSERY, VINEYARD, STATUE, ARCHERY_RANGE, DISPENSARY, WALLS, FOUNDRY, LABORATORY, LIBRARY, STABLES, TEMPLE, AQUEDUCT, COURTHOUSE, FORUM, SCHOOL, GLASSWORKS, BRICKYARD, LOOM, QUARRY, SAWMILL, PRESS),
        4 -> Multiset(BAZAR, TRAINING_GROUND, DISPENSARY, BRICKYARD, FOUNDRY, QUARRY, SAWMILL),
        5 -> Multiset(GLASSWORKS, COURTHOUSE, LABORATORY, CARAVANSERY, STABLES, PRESS, LOOM),
        6 -> Multiset(CARAVANSERY, FORUM, VINEYARD, ARCHERY_RANGE, LIBRARY, TEMPLE, TRAINING_GROUND),
        7 -> Multiset(AQUEDUCT, STATUE, FORUM, BAZAR, SCHOOL, WALLS, TRAINING_GROUND)
      ),
      3 -> Map(
        3 -> Multiset(LODGE, OBSERVATORY, SIEGE_WORKSHOP, ARENA, SENATE, ARSENAL, ACADEMY, TOWN_HALL, PANTHEON, PALACE, HAVEN, LIGHTHOUSE, UNIVERSITY, GARDENS, FORTIFICATIONS, STUDY),
        4 -> Multiset(UNIVERSITY, ARSENAL, GARDENS, HAVEN, CIRCUS, CHAMBER_OF_COMMERCE),
        5 -> Multiset(ARENA, TOWN_HALL, CIRCUS, SIEGE_WORKSHOP, SENATE),
        6 -> Multiset(TOWN_HALL, CIRCUS, LODGE, PANTHEON, CHAMBER_OF_COMMERCE, LIGHTHOUSE),
        7 -> Multiset(ARENA, OBSERVATORY, ACADEMY, FORTIFICATIONS, ARSENAL, PALACE)
      )
    ),
    Set(STRATEGISTS_GUILD, TRADERS_GUILD, MAGISTRATES_GUILD, SHOPOWNERS_GUILD, CRAFTMENS_GUILD, WORKERS_GUILD, PHILOSOPHERS_GUILD, SCIENTISTS_GUILD, SPIES_GUILD, BUILDERS_GUILD)
  )
}