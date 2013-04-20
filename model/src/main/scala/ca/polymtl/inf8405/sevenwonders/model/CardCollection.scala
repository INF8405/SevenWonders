package ca.polymtl.inf8405.sevenwonders.model

import collection.MultiSet

object CardCollection {

  import Resource._

  ////
  // AGE I
  ////

  // Commercial Cards
  val TAVERN = CommercialCard("TAVERN", Free, Set(), Set(CoinSymbol(SimpleAmount(5))))
  val WEST_TRADING_POST = CommercialCard("WEST_TRADING_POST", Free, Set(FORUM), Set(RebateSymbol(Set(Clay, Stone, Wood, Ore), Set(Left))))
  val MARKETPLACE = CommercialCard("MARKETPLACE", Free, Set(CARAVANSERY), Set(RebateSymbol(Set(Glass, Tapestry, Paper), Set(Left, Right))))
  val EAST_TRADING_POST = CommercialCard("EAST_TRADING_POST", Free, Set(FORUM), Set(RebateSymbol(Set(Clay, Stone, Wood, Ore), Set(Right))))

  // Military Cards
  val STOCKADE = MilitaryCard("STOCKADE", Cost(0, MultiSet(Wood)), Set(), 1)
  val BARRACKS = MilitaryCard("BARRACKS", Cost(0, MultiSet(Ore)), Set(), 1)
  val GUARD_TOWER = MilitaryCard("GUARD_TOWER", Cost(0, MultiSet(Clay)), Set(), 1)

  // Science Cards
  val WORKSHOP = ScienceCard("WORKSHOP", Cost(0, MultiSet(Glass)), Set(LABORATORY, ARCHERY_RANGE), Gear)
  val SCRIPTORIUM = ScienceCard("SCRIPTORIUM", Cost(0, MultiSet(Paper)), Set(COURTHOUSE, LIBRARY), Tablet)
  val APOTHECARY = ScienceCard("APOTHECARY", Cost(0, MultiSet(Tapestry)), Set(STABLES, DISPENSARY), Compass)

  // Civilian Cards
  val THEATER = CivilianCard("THEATER", Free, Set(STATUE), 2)
  val BATHS = CivilianCard("BATHS", Cost(0, MultiSet(Stone)), Set(AQUEDUCT), 3)
  val ALTAR = CivilianCard("ALTAR", Free, Set(TEMPLE), 2)
  val PAWNSHOP = CivilianCard("PAWNSHOP", Free, Set(), 3)

  // Raw Material Cards
  val TREE_FARM = new RawMaterialCard("TREE_FARM", new Cost(1), Wood | Clay)
  val MINE = new RawMaterialCard("MINE", new Cost(1), Stone | Ore)
  val CLAY_PIT = new RawMaterialCard("CLAY_PIT",new Cost(1), Clay | Ore)
  val TIMBER_YARD = new RawMaterialCard("TIMBER_YARD", new Cost(1), Stone | Wood)
  val STONE_PIT = new RawMaterialCard("STONE_PIT", Free, Stone)
  val FOREST_CAVE = new RawMaterialCard("FOREST_CAVE", new Cost(1), Wood | Ore)
  val LUMBER_YARD = new RawMaterialCard("LUMBER_YARD", Free, Wood)
  val ORE_VEIN = new RawMaterialCard("ORE_VEIN", Free, Ore)
  val EXCAVATION = new RawMaterialCard("EXCAVATION", new Cost(1), Stone | Clay)
  val CLAY_POOL = new RawMaterialCard("CLAY_POOL", Free, Clay)

  // Manufactured Good Cards
  val LOOM = new ManufacturedGoodCard("LOOM", Tapestry)
  val GLASSWORKS = new ManufacturedGoodCard("GLASSWORKS", Glass)
  val PRESS = new ManufacturedGoodCard("PRESS", Paper)

  ////
  // AGE II
  ////

  // Commercial Cards
  val CARAVANSERY = CommercialCard("CARAVANSERY", Cost(0, MultiSet(Wood, Wood)), Set(LIGHTHOUSE), Set(Wood | Stone | Ore | Clay))
  val FORUM = CommercialCard("FORUM", Cost(0, MultiSet(Clay, Clay)), Set(HAVEN), Set(Glass | Tapestry | Paper))
  val BAZAR = CommercialCard("BAZAR", Free, Set(), Set(CoinSymbol(VariableAmount(2, classOf[ManufacturedGoodCard], Set(Left, Self, Right)))))
  val VINEYARD = CommercialCard("VINEYARD", Free, Set(), Set(CoinSymbol(VariableAmount(1, classOf[RawMaterialCard], Set(Left, Self, Right)))))

  // Military Cards
  val WALLS = MilitaryCard("WALLS", Cost(0, MultiSet(Stone, Stone, Stone)), Set(FORTIFICATIONS), 2)
  val ARCHERY_RANGE = MilitaryCard("ARCHERY_RANGE", Cost(0, MultiSet(Wood, Wood, Ore)), Set(), 2)
  val TRAINING_GROUND = MilitaryCard("TRAINING_GROUND", Cost(0, MultiSet(Ore, Ore, Wood)), Set(CIRCUS), 2)
  val STABLES = MilitaryCard("STABLES", Cost(0, MultiSet(Clay, Wood, Ore)), Set(), 2)

  // Science Cards
  val SCHOOL = ScienceCard("SCHOOL", Cost(0, MultiSet(Wood, Paper)), Set(ACADEMY, STUDY), Tablet)
  val LIBRARY = ScienceCard("LIBRARY", Cost(0, MultiSet(Stone, Stone, Tapestry)), Set(SENATE, UNIVERSITY), Tablet)
  val LABORATORY = ScienceCard("LABORATORY", Cost(0, MultiSet(Clay, Clay, Paper)), Set(OBSERVATORY, SIEGE_WORKSHOP),Gear)
  val DISPENSARY = ScienceCard("DISPENSARY", Cost(0, MultiSet(Ore, Ore, Glass)), Set(LODGE, ARENA), Compass)

  // Civilian Cards
  val AQUEDUCT = CivilianCard("AQUEDUC", Cost(0, MultiSet(Stone, Stone, Stone)), Set(), 5)
  val STATUE = CivilianCard("STATUE", Cost(0, MultiSet(Ore, Ore, Wood)), Set(GARDENS), 4)
  val TEMPLE = CivilianCard("TEMPLE", Cost(0, MultiSet(Wood, Clay, Glass)), Set(PANTHEON), 3)
  val COURTHOUSE = CivilianCard("COURTHOUSE", Cost(0, MultiSet(Clay, Clay, Tapestry)), Set(), 4)

  // Raw Material Cards
  val FOUNDRY = new RawMaterialCard("FOUNDRY", new Cost(1), Ore + Ore)
  val QUARRY = new RawMaterialCard("QUARRY", new Cost(1), Stone + Stone)
  val BRICKYARD = new RawMaterialCard("BRICKYARD", new Cost(1), Clay + Clay)
  val SAWMILL = new RawMaterialCard("SAWMILL", new Cost(1), Wood + Wood)

  ////
  // AGE III
  ////

  // Commercial Cards
  val ARENA = CommercialCard("ARENA", Cost(0, MultiSet(Stone, Stone, Ore)), Set(), Set(CoinSymbol(VariableAmount(3, classOf[WonderStage], Set(Self))), VictoryPointSymbol(VariableAmount(1, classOf[WonderStage], Set(Self)))))
  val CHAMBER_OF_COMMERCE = CommercialCard("CHAMBER_OF_COMMERCE", Cost(0, MultiSet(Clay, Clay, Paper)), Set(), Set(VictoryPointSymbol(VariableAmount(2, classOf[ManufacturedGoodCard], Set(Self))), CoinSymbol(VariableAmount(2, classOf[ManufacturedGoodCard], Set(Self)))))
  val LIGHTHOUSE = CommercialCard("LIGHTHOUSE", Cost(0, MultiSet(Stone, Glass)), Set(), Set(CoinSymbol(VariableAmount(1, classOf[CommercialCard], Set(Self))), VictoryPointSymbol(VariableAmount(1, classOf[CommercialCard], Set(Self)))))
  val HAVEN = CommercialCard("HAVEN", Cost(0, MultiSet(Wood, Ore, Tapestry)), Set(), Set(CoinSymbol(VariableAmount(1, classOf[RawMaterialCard], Set(Self))), VictoryPointSymbol(VariableAmount(1, classOf[RawMaterialCard], Set(Self)))))

  // Military Cards
  val CIRCUS = MilitaryCard("CIRCUS", Cost(0, MultiSet(Stone, Stone, Stone, Ore)), Set(), 3)
  val FORTIFICATIONS = MilitaryCard("FORTIFICATIONS", Cost(0, MultiSet(Ore, Ore, Ore, Stone)), Set(), 3)
  val ARSENAL = MilitaryCard("ARSENAL", Cost(0, MultiSet(Wood, Wood, Ore, Tapestry)), Set(), 3)
  val SIEGE_WORKSHOP = MilitaryCard("SIEGE WORKSHOP", Cost(0, MultiSet(Clay, Clay, Clay, Wood)), Set(), 3)

  // Science Cards
  val OBSERVATORY = ScienceCard("OBSERVATORY", Cost(0, MultiSet(Ore, Ore, Glass, Tapestry)), Set(), Gear)
  val ACADEMY = ScienceCard("ACADEMY", Cost(0, MultiSet(Stone, Stone, Stone)), Set(), Compass)
  val LODGE = ScienceCard("LODGE", Cost(0, MultiSet(Clay, Clay, Paper, Tapestry)), Set(), Compass)
  val UNIVERSITY = ScienceCard("UNIVERSITY", Cost(0, MultiSet(Wood, Wood, Paper, Glass)), Set(), Tablet)
  val STUDY = ScienceCard("STUDY", Cost(0, MultiSet(Wood, Paper, Tapestry)), Set(), Gear)

  // Civilian Cards
  val TOWN_HALL = CivilianCard("TOWN HALL", Cost(0, MultiSet(Stone, Stone, Ore, Glass)), Set(), 6)
  val PALACE = CivilianCard("PALACE", Cost(0, MultiSet(Stone, Ore, Wood, Clay, Glass, Paper, Tapestry)), Set(), 8)
  val PANTHEON = CivilianCard("PANTHEON", Cost(0, MultiSet(Clay, Clay, Ore, Glass, Paper, Tapestry)), Set(), 7)
  val GARDENS = CivilianCard("GARDENS", Cost(0, MultiSet(Clay, Clay, Wood)), Set(), 5)
  val SENATE = CivilianCard("SENATE", Cost(0, MultiSet(Wood, Wood, Stone, Ore)), Set(), 6)

  // Guilds
  val STRATEGISTS_GUILD = GuildCard("STARTEGISTS_GUILD", Cost(0, MultiSet(Ore, Ore, Stone, Tapestry)), Set(VictoryPointSymbol(VariableAmount(1, classOf[DefeatBattleMarker], Set(Left, Right)))))
  val TRADERS_GUILD = GuildCard("TRADERS_GUILD", Cost(0, MultiSet(Glass, Tapestry, Paper)), Set(VictoryPointSymbol(VariableAmount(1, classOf[CommercialCard], Set(Left, Right)))))
  val MAGISTRATES_GUILD = GuildCard("MAGISTRATES_GUILD", Cost(0, MultiSet(Wood, Wood, Wood, Stone, Tapestry)), Set(VictoryPointSymbol(VariableAmount(1, classOf[CivilianCard], Set(Left, Right)))))
  val SHIPOWNERS_GUILD = GuildCard("SHIPOWNERS_GUILD", Cost(0, MultiSet(Wood, Wood, Wood, Glass, Paper)), Set(VictoryPointSymbol(VariableAmount(1, classOf[RawMaterialCard], Set(Self))), VictoryPointSymbol(VariableAmount(1, classOf[ManufacturedGoodCard], Set(Self))), VictoryPointSymbol(VariableAmount(1, classOf[GuildCard], Set(Self)))))
  val CRAFTMENS_GUILD = GuildCard("CRAFTSMENS_GUILD", Cost(0, MultiSet(Ore, Ore, Stone, Stone)), Set(VictoryPointSymbol(VariableAmount(2, classOf[ManufacturedGoodCard], Set(Left, Right)))))
  val WORKERS_GUILD = GuildCard("WORKERS_GUILD", Cost(0, MultiSet(Ore, Ore, Clay, Stone, Wood)), Set(VictoryPointSymbol(VariableAmount(1, classOf[RawMaterialCard], Set(Left, Right)))))
  val PHILOSOPHERS_GUILD = GuildCard("PHILOSOPHERS_GUILD", Cost(0, MultiSet(Clay, Clay, Clay, Paper, Tapestry)), Set(VictoryPointSymbol(VariableAmount(1, classOf[ScienceCard], Set(Left, Right)))))
  val SCIENTISTS_GUILD = GuildCard("SCIENTISTS_GUILD", Cost(0, MultiSet(Wood, Wood, Ore, Ore, Paper)), Set(Gear | Tablet | Compass))
  val SPIES_GUILD = GuildCard("SPIES_GUILD", Cost(0, MultiSet(Clay, Clay, Clay, Glass)), Set(VictoryPointSymbol(VariableAmount(1, classOf[MilitaryCard], Set(Left, Right)))))
  val BUILDERS_GUILD = GuildCard("BUILDERS_GUILD", Cost(0, MultiSet(Stone, Stone, Clay, Clay, Glass)), Set(VictoryPointSymbol(VariableAmount(1, classOf[WonderStage], Set(Left, Self, Right)))))

  // Cities Cards
  val PIGEON_LOFT = CityCard("PIGEON_LOFT", Cost(1, MultiSet(Ore)), Set(new StealScience))
  val SPY_RING = CityCard("SPY_RING", Cost(2, MultiSet(Stone, Clay)), Set(new StealScience))
  val TORTURE_CHAMBER = CityCard("TORTURE_CHAMBER", Cost(3, MultiSet(Glass, Ore, Ore)), Set(new StealScience))
  val RESIDENCE = CityCard("RESIDENCE", Cost(0, MultiSet(Clay)), Set(VictoryPointSymbol(1), DiplomacySymbol))
  val CONSULATE = CityCard("CONSULATE", Cost(0, MultiSet(Paper, Clay)), Set(VictoryPointSymbol(2), DiplomacySymbol))
  val EMBASSY = CityCard("EMBASSY", Cost(0, MultiSet(Tapestry, Stone, Paper)), Set(VictoryPointSymbol(3), DiplomacySymbol))
  val HIDEOUT = CityCard("HIDEOUT", Free, Set(VictoryPointSymbol(2), PayBankSymbol(1)))
  val LAIR = CityCard("LAIR", Cost(0, MultiSet(Wood, Glass)), Set(VictoryPointSymbol(3), PayBankSymbol(2)))
  val BROTHERHOOD = CityCard("BROTHERHOOD", Cost(0, MultiSet(Wood, Wood, Tapestry, Ore)), Set(VictoryPointSymbol(4), PayBankSymbol(3)))
  val CLANDESTINE_DOCK_WEST = CityCard("CLANDESTINE_DOCK_WEST", new Cost(1), Set(DiscountSymbol(allResources, Set(Left))))
  val CLANDESTINE_DOCK_EAST = CityCard("CLANDESTINE_DOCK_EAST", new Cost(1), Set(DiscountSymbol(allResources, Set(Right))))
  val GAMBLING_DEN = CityCard("GAMBLING_DEN",Free, Set(CoinSymbol(ThreeWayAmount(1, 6, 1))))
  val GAMBLING_HOUSE = CityCard("GAMBLING_HOUSE",new Cost(1), Set(CoinSymbol(ThreeWayAmount(2, 8, 2))))
  val BLACK_MARKET = CityCard("BLACK_MARKET", Cost(0, MultiSet(Ore, Tapestry)), Set(ProduceResourceNotProduced))
  val SECRET_WAREHOUSE = CityCard("SECRET_WAREHOUSE", new Cost(2), Set(ProduceResourceAlreadyProduced))
  val ARCHITECT_CABINET = CityCard("ARCHITECT_CABINET", Cost(1, MultiSet(Paper)), Set(VictoryPointSymbol(2), BuildWondersForFree))
  val BUILDERS_UNION = CityCard("BUILDERS_UNION", Cost(0, MultiSet(Clay, Wood, Paper, Glass)), Set(VictoryPointSymbol(4), PayBankSymbol(VariableAmount(1, classOf[WonderStage], Set(Self)))))
  val SEPULCHER = CityCard("SEPULCHER", Cost(0, MultiSet(Stone, Glass, Tapestry)), Set(VictoryPointSymbol(4), PayBankSymbol(VariableAmount(1, classOf[VictoryBattleMarker], Set(Self)))))
  val CENOTAPH = CityCard("CENOTAPH", Cost(0, MultiSet(Clay, Clay, Stone, Tapestry, Glass)), Set(VictoryPointSymbol(5), PayBankSymbol(VariableAmount(1, classOf[VictoryBattleMarker], Set(Self)))))
  val SECRET_SOCIETY = CityCard("SECRET_SOCIETY", Cost(0, MultiSet(Stone, Paper)), Set(CoinSymbol(VariableAmount(1, classOf[CityCard], Set(Self))), VictoryPointSymbol(VariableAmount(1, classOf[CityCard], Set(Self)))))
  val SLAVE_MARKET = CityCard("SLAVE_MARKET", Cost(0, MultiSet(Ore, Ore, Wood, Wood)), Set(CoinSymbol(VariableAmount(1, classOf[VictoryBattleMarker], Set(Self))), VictoryPointSymbol(VariableAmount(1, classOf[VictoryBattleMarker], Set(Self)))))
  val MILITIA = CityCard("MILITIA", new Cost(3), Set(MilitarySymbol(2)))
  val MERCENARIES = CityCard("MERCENARIES", Cost(4, MultiSet(Paper)), Set(MilitarySymbol(3)))
  val CONTINGENT = CityCard("CONTINGENT", Cost(5, MultiSet(Tapestry)), Set(MilitarySymbol(5)))
  val GATES_OF_THE_CITY = CityCard("GATES_OF_THE_CITY", Cost(1, MultiSet(Wood)), Set(VictoryPointSymbol(4)))
  val TABULARIUM = CityCard("TABULARIUM", Cost(2, MultiSet(Ore, Wood, Tapestry)), Set(VictoryPointSymbol(6)))
  val CAPITOL = CityCard("CAPITOL", Cost(2, MultiSet(Clay, Clay, Stone, Stone, Glass, Paper)), Set(VictoryPointSymbol(8)))

  val MOURNERS_GUILD = GuildCard("MOURNERS_GUILD", Cost(0, MultiSet(Clay, Clay, Wood, Glass, Tapestry)), Set(VictoryPointSymbol(VariableAmount(1, classOf[VictoryBattleMarker], Set(Left, Right)))))
  val COUNTERFEITERS_GUILD = GuildCard("COUNTERFEITERS_GUILD", Cost(0, MultiSet(Ore, Ore, Ore, Glass, Tapestry)), Set(VictoryPointSymbol(5), PayBankSymbol(3)))
  val GUILD_OF_SHADOWS = GuildCard("GUILD_OF_SHADOWS", Cost(0, MultiSet(Stone, Stone, Wood, Paper)), Set(VictoryPointSymbol(VariableAmount(1, classOf[CityCard], Set(Left, Right)))))

  val normalBaseCards: Map[Age, Map[PlayerAmount, MultiSet[Card]]] =
    Map(
      1 -> Map(
        3 -> MultiSet(APOTHECARY, CLAY_POOL, ORE_VEIN, WORKSHOP, SCRIPTORIUM, BARRACKS, EAST_TRADING_POST, STOCKADE, CLAY_PIT, LOOM, GLASSWORKS, THEATER, BATHS, TIMBER_YARD, PRESS, STONE_PIT, MARKETPLACE, GUARD_TOWER, WEST_TRADING_POST, ALTAR, LUMBER_YARD),
        4 -> MultiSet(GUARD_TOWER, LUMBER_YARD, PAWNSHOP, TAVERN, SCRIPTORIUM, EXCAVATION, ORE_VEIN),
        5 -> MultiSet(CLAY_POOL, ALTAR, APOTHECARY, BARRACKS, STONE_PIT, TAVERN, FOREST_CAVE),
        6 -> MultiSet(THEATER, PRESS, GLASSWORKS, LOOM, MARKETPLACE, MINE, TREE_FARM),
        7 -> MultiSet(WORKSHOP, EAST_TRADING_POST, STOCKADE, BATHS, WEST_TRADING_POST, TAVERN, PAWNSHOP)
      ),
      2 -> Map(
        3 -> MultiSet(CARAVANSERY, VINEYARD, STATUE, ARCHERY_RANGE, DISPENSARY, WALLS, FOUNDRY, LABORATORY, LIBRARY, STABLES, TEMPLE, AQUEDUCT, COURTHOUSE, FORUM, SCHOOL, GLASSWORKS, BRICKYARD, LOOM, QUARRY, SAWMILL, PRESS),
        4 -> MultiSet(BAZAR, TRAINING_GROUND, DISPENSARY, BRICKYARD, FOUNDRY, QUARRY, SAWMILL),
        5 -> MultiSet(GLASSWORKS, COURTHOUSE, LABORATORY, CARAVANSERY, STABLES, PRESS, LOOM),
        6 -> MultiSet(CARAVANSERY, FORUM, VINEYARD, ARCHERY_RANGE, LIBRARY, TEMPLE, TRAINING_GROUND),
        7 -> MultiSet(AQUEDUCT, STATUE, FORUM, BAZAR, SCHOOL, WALLS, TRAINING_GROUND)
      ),
      3 -> Map(
        3 -> MultiSet(LODGE, OBSERVATORY, SIEGE_WORKSHOP, ARENA, SENATE, ARSENAL, ACADEMY, TOWN_HALL, PANTHEON, PALACE, HAVEN, LIGHTHOUSE, UNIVERSITY, GARDENS, FORTIFICATIONS, STUDY),
        4 -> MultiSet(UNIVERSITY, ARSENAL, GARDENS, HAVEN, CIRCUS, CHAMBER_OF_COMMERCE),
        5 -> MultiSet(ARENA, TOWN_HALL, CIRCUS, SIEGE_WORKSHOP, SENATE),
        6 -> MultiSet(TOWN_HALL, CIRCUS, LODGE, PANTHEON, CHAMBER_OF_COMMERCE, LIGHTHOUSE),
        7 -> MultiSet(ARENA, OBSERVATORY, ACADEMY, FORTIFICATIONS, ARSENAL, PALACE)
      )
    )

  val baseGuilds =
    Set(
      STRATEGISTS_GUILD,
      TRADERS_GUILD,
      MAGISTRATES_GUILD,
      SHIPOWNERS_GUILD,
      CRAFTMENS_GUILD,
      WORKERS_GUILD,
      PHILOSOPHERS_GUILD,
      SCIENTISTS_GUILD,
      SPIES_GUILD,
      BUILDERS_GUILD
    )

  val cityGuilds =
    Set(
      MOURNERS_GUILD,
      COUNTERFEITERS_GUILD,
      GUILD_OF_SHADOWS
    )

  val cityCards =
    Map(
      1 -> Set(
        PIGEON_LOFT,
        RESIDENCE,
        HIDEOUT,
        CLANDESTINE_DOCK_EAST,
        CLANDESTINE_DOCK_WEST,
        GAMBLING_DEN,
        MILITIA,
        GATES_OF_THE_CITY
      ),
      2 -> Set(
        SPY_RING,
        CONSULATE,
        LAIR,
        GAMBLING_HOUSE,
        BLACK_MARKET,
        SECRET_WAREHOUSE,
        ARCHITECT_CABINET,
        MERCENARIES,
        TABULARIUM
      ),
      3 -> Set(
        TORTURE_CHAMBER,
        EMBASSY,
        BROTHERHOOD,
        BUILDERS_UNION,
        CENOTAPH,
        SLAVE_MARKET,
        CONTINGENT,
        CAPITOL
      )
    )

  object DUMMY_CARD extends Card( "DUMMY_CARD", Free, Set.empty, Set.empty )
}
