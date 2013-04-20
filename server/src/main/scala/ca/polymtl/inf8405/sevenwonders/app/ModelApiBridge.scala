package ca.polymtl.inf8405.sevenwonders
package app

import api.Card._
import api.Resource._
import ca.polymtl.inf8405.sevenwonders.model.{CardCollection => CC, Right, Left}


import com.google.common.collect.ImmutableBiMap
import ca.polymtl.inf8405.sevenwonders.api.NeighborReference

object ModelApiBridge
{
  val cardsBridge = BiMap(
    /* === Age I === */

    // Commercial
    TAVERN -> CC.TAVERN,
    WEST_TRADING_POST -> CC.WEST_TRADING_POST,
    MARKETPLACE -> CC.MARKETPLACE, 
    EAST_TRADING_POST -> CC.EAST_TRADING_POST, 

    // Military
    STOCKADE -> CC.STOCKADE, 
    BARRACKS -> CC.BARRACKS, 
    GUARD_TOWER -> CC.GUARD_TOWER,

    // Science
    WORKSHOP -> CC.WORKSHOP, 
    SCRIPTORIUM -> CC.SCRIPTORIUM, 
    APOTHECARY -> CC.APOTHECARY, 

    // Civilian
    THEATER -> CC.THEATER, 
    BATHS -> CC.BATHS, 
    ALTAR -> CC.ALTAR, 
    PAWNSHOP -> CC.PAWNSHOP, 

    // Raw Material
    TREE_FARM -> CC.TREE_FARM, 
    MINE -> CC.MINE, 
    CLAY_PIT -> CC.CLAY_PIT, 
    TIMBER_YARD -> CC.TIMBER_YARD, 
    STONE_PIT -> CC.STONE_PIT, 
    FOREST_CAVE -> CC.FOREST_CAVE, 
    LUMBER_YARD -> CC.LUMBER_YARD, 
    ORE_VEIN -> CC.ORE_VEIN, 
    EXCAVATION -> CC.EXCAVATION, 
    CLAY_POOL -> CC.CLAY_POOL, 

    // Manufactured Good
    LOOM -> CC.LOOM, 
    GLASSWORKS -> CC.GLASSWORKS, 
    PRESS -> CC.PRESS, 

    /* === Age II === */

    // Commercial
    CARAVANSERY -> CC.CARAVANSERY, 
    FORUM -> CC.FORUM, 
    BAZAR -> CC.BAZAR, 
    VINEYARD -> CC.VINEYARD, 

    // Military
    WALLS -> CC.WALLS, 
    ARCHERY_RANGE -> CC.ARCHERY_RANGE,
    TRAINING_GROUND -> CC.TRAINING_GROUND,
    STABLES -> CC.STABLES,

    // Science
    SCHOOL -> CC.SCHOOL,
    LIBRARY -> CC.LIBRARY,
    LABORATORY -> CC.LABORATORY,
    DISPENSARY -> CC.DISPENSARY,

    // Civilian
    AQUEDUCT -> CC.AQUEDUCT,
    STATUE -> CC.STATUE,
    TEMPLE -> CC.TEMPLE,
    COURTHOUSE -> CC.COURTHOUSE,

    // Raw Material
    FOUNDRY -> CC.FOUNDRY,
    QUARRY -> CC.QUARRY,
    BRICKYARD -> CC.BRICKYARD,
    SAWMILL -> CC.SAWMILL,

    /* === Age III === */

    // Commercial
    ARENA -> CC.ARENA,
    CHAMBER_OF_COMMERCE -> CC.CHAMBER_OF_COMMERCE,
    LIGHTHOUSE -> CC.LIGHTHOUSE,
    HAVEN -> CC.HAVEN,

    // Military
    CIRCUS -> CC.CIRCUS,
    FORTIFICATIONS -> CC.FORTIFICATIONS,
    ARSENAL -> CC.ARSENAL,
    SIEGE_WORKSHOP -> CC.SIEGE_WORKSHOP,

    // Science
    OBSERVATORY -> CC.OBSERVATORY,
    ACADEMY -> CC.ACADEMY,
    LODGE -> CC.LODGE,
    UNIVERSITY -> CC.UNIVERSITY,
    STUDY -> CC.STUDY,

    // Civilian
    TOWN_HALL -> CC.TOWN_HALL,
    PALACE -> CC.PALACE,
    PANTHEON -> CC.PANTHEON,
    GARDENS -> CC.GARDENS,
    SENATE -> CC.SENATE,

    // Guilds
    STRATEGISTS_GUILD -> CC.STRATEGISTS_GUILD,
    TRADERS_GUILD -> CC.TRADERS_GUILD,
    MAGISTRATES_GUILD -> CC.MAGISTRATES_GUILD,
    SHIPOWNERS_GUILD -> CC.SHIPOWNERS_GUILD,
    CRAFTMENS_GUILD -> CC.CRAFTMENS_GUILD,
    WORKERS_GUILD -> CC.WORKERS_GUILD,
    PHILOSOPHERS_GUILD -> CC.PHILOSOPHERS_GUILD,
    SCIENTISTS_GUILD -> CC.SCIENTISTS_GUILD,
    SPIES_GUILD -> CC.SPIES_GUILD,
    BUILDERS_GUILD -> CC.BUILDERS_GUILD
  )
  cardsBridge.inverse() // hit the cash

  val bridgeResource = BiMap(
    api.Resource.CLAY -> model.Resource.Clay,
    api.Resource.WOOD -> model.Resource.Wood,
    api.Resource.ORE -> model.Resource.Ore,
    api.Resource.STONE -> model.Resource.Stone,
    api.Resource.GLASS -> model.Resource.Glass,
    api.Resource.PAPER -> model.Resource.Paper,
    api.Resource.TAPESTRY -> model.Resource.Tapestry
  )
  bridgeResource.inverse()

  val bridgeNeighbor = BiMap(
    NeighborReference.LEFT -> Left,
    NeighborReference.RIGHT -> Right
  )
  bridgeNeighbor.inverse()
}

object BiMap {
  def apply[A,B]( elems: (A,B)* ) = {

    elems.foldLeft( new ImmutableBiMap.Builder[A,B]() ) { case ( builder, ( a1, a2 ) ) =>
      builder.put( a1, a2 )
    }.build()
  }
}
