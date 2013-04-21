package ca.polymtl.inf8405.sevenwonders.database;

import java.util.*;

import android.content.Context;
import android.util.Log;

import ca.polymtl.inf8405.sevenwonders.api.*;
import static ca.polymtl.inf8405.sevenwonders.api.Card.*;
import static ca.polymtl.inf8405.sevenwonders.api.Civilisation.*;

import ca.polymtl.inf8405.sevenwonders.R;

public class Database {
	private static Map<Card, Integer> cardMapper_;
	private static Map<Civilisation, Integer> civilisationMapper_;
	private static Database instance_;

	private Database(){
		setupCardMapper();
		setupCivilisation();
	}

	public static Database getInstance(){
		if (instance_ == null)
			instance_ = new Database();
		return instance_;
	}

	public Integer getBitmapId(Card card){
		if (cardMapper_.containsKey(card))
			return cardMapper_.get(card);
		else
			return 0;
	}
	
	public Integer getCivilisationBitmapId(Civilisation civilisation){
		if (civilisationMapper_.containsKey(civilisation))
			return civilisationMapper_.get(civilisation);
		else
			return 0;
	}
	
	private static void setupCardMapper(){
		cardMapper_ = new HashMap<Card, Integer>();

        // === Age I ===
        // Commercial
        cardMapper_.put(TAVERN, R.drawable.tavern);
        cardMapper_.put(WEST_TRADING_POST, R.drawable.west_trading_post);
        cardMapper_.put(MARKETPLACE, R.drawable.marketplace);
        cardMapper_.put(EAST_TRADING_POST, R.drawable.east_trading_port);


        // Military
        cardMapper_.put(STOCKADE, R.drawable.stockade);
        cardMapper_.put(BARRACKS, R.drawable.baracks);
        cardMapper_.put(GUARD_TOWER, R.drawable.guard_tower);

        // Science
        cardMapper_.put(WORKSHOP, R.drawable.workshop);
        cardMapper_.put(SCRIPTORIUM, R.drawable.scriptorium);
        cardMapper_.put(APOTHECARY, R.drawable.apothecary);

        // Civilian
        cardMapper_.put(THEATER, R.drawable.theater);
        cardMapper_.put(BATHS, R.drawable.baths);
        cardMapper_.put(ALTAR, R.drawable.altar);
        cardMapper_.put(PAWNSHOP, R.drawable.pawnshop);

        // Raw Material
        cardMapper_.put(TREE_FARM, R.drawable.tree_farm);
        cardMapper_.put(MINE, R.drawable.mine);
        cardMapper_.put(CLAY_PIT, R.drawable.clay_pit);
        cardMapper_.put(TIMBER_YARD, R.drawable.timber_yard);
        cardMapper_.put(STONE_PIT, R.drawable.stone_pit);
        cardMapper_.put(FOREST_CAVE, R.drawable.forest_cave);
        cardMapper_.put(LUMBER_YARD, R.drawable.lumber_yard);
        cardMapper_.put(ORE_VEIN, R.drawable.ore_vein);
        cardMapper_.put(EXCAVATION, R.drawable.excavation);
        cardMapper_.put(CLAY_POOL, R.drawable.clay_pool);

        // Manufactured Good
        cardMapper_.put(LOOM, R.drawable.loom);
        cardMapper_.put(GLASSWORKS, R.drawable.glassworks);
        cardMapper_.put(PRESS, R.drawable.press);

        // === Age II ===

        // Commercial
//        cardMapper_.put(CARAVANSERY, R.drawable.caravansery);
//        cardMapper_.put(FORUM, R.drawable.forum);
//        cardMapper_.put(BAZAR, R.drawable.bazar);
//        cardMapper_.put(VINEYARD, R.drawable.vineyard);
//
//        // Military
//        cardMapper_.put(WALLS, R.drawable.walls);
//        cardMapper_.put(ARCHERY_RANGE, R.drawable.archery_range);
//        cardMapper_.put(TRAINING_GROUND, R.drawable.training_ground);
//        cardMapper_.put(STABLES, R.drawable.stables);
//
//        // Science
//        cardMapper_.put(SCHOOL, R.drawable.school);
//        cardMapper_.put(LIBRARY, R.drawable.library);
//        cardMapper_.put(LABORATORY, R.drawable.laboratory);
//        cardMapper_.put(DISPENSARY, R.drawable.dispensary);
//
//        // Civilian
//        cardMapper_.put(AQUEDUCT, R.drawable.aqueduct);
//        cardMapper_.put(STATUE, R.drawable.statue);
//        cardMapper_.put(TEMPLE, R.drawable.temple);
//        cardMapper_.put(COURTHOUSE, R.drawable.courthouse);
//
//        // Raw Material
//        cardMapper_.put(FOUNDRY, R.drawable.foundry);
//        cardMapper_.put(QUARRY, R.drawable.quarry);
//        cardMapper_.put(BRICKYARD, R.drawable.brickyard);
//        cardMapper_.put(SAWMILL, R.drawable.sawmill);
//
//        // === Age III ===
//
//        // Commercial
//        cardMapper_.put(ARENA, R.drawable.arena);
//        cardMapper_.put(CHAMBER_OF_COMMERCE, R.drawable.chamber_of_commerce);
//        cardMapper_.put(LIGHTHOUSE, R.drawable.lighthouse);
//        cardMapper_.put(HAVEN, R.drawable.haven);
//
//        // Military
//        cardMapper_.put(CIRCUS, R.drawable.circus);
//        cardMapper_.put(FORTIFICATIONS, R.drawable.fortifications);
//        cardMapper_.put(ARSENAL, R.drawable.arsenal);
//        cardMapper_.put(SIEGE_WORKSHOP, R.drawable.siege_workshop);
//
//        // Science
//        cardMapper_.put(OBSERVATORY, R.drawable.observatory);
//        cardMapper_.put(ACADEMY, R.drawable.academy);
//        cardMapper_.put(LODGE, R.drawable.lodge);
//        cardMapper_.put(UNIVERSITY, R.drawable.university);
//        cardMapper_.put(STUDY, R.drawable.study);
//
//        // Civilian
//        cardMapper_.put(TOWN_HALL, R.drawable.townhall);
//        cardMapper_.put(PALACE, R.drawable.palace);
//        cardMapper_.put(PANTHEON, R.drawable.pantheon);
//        cardMapper_.put(GARDENS, R.drawable.gardens);
//        cardMapper_.put(SENATE, R.drawable.senate);
//
//        // Guilds
//        cardMapper_.put(STRATEGISTS_GUILD, R.drawable.stategists_guild);
//        cardMapper_.put(TRADERS_GUILD, R.drawable.traders_guild);
//        cardMapper_.put(MAGISTRATES_GUILD, R.drawable.magistrates_guild);
//        cardMapper_.put(SHIPOWNERS_GUILD, R.drawable.shipowners_guild);
//        cardMapper_.put(CRAFTMENS_GUILD, R.drawable.craftmens_guild);
//        cardMapper_.put(WORKERS_GUILD, R.drawable.workers_guild);
//        cardMapper_.put(PHILOSOPHERS_GUILD, R.drawable.philosophers_guild);
//        cardMapper_.put(SCIENTISTS_GUILD, R.drawable.scientists_guild);
//        cardMapper_.put(SPIES_GUILD, R.drawable.spies_guild);
//        cardMapper_.put(BUILDERS_GUILD, R.drawable.builders_guild);
	}

	private static void setupCivilisation(){
		civilisationMapper_ = new HashMap<Civilisation, Integer>();

        civilisationMapper_.put(RHODOS_A, R.drawable.rhodos_a);
        civilisationMapper_.put(RHODOS_B, R.drawable.rhodos_b);
        civilisationMapper_.put(ALEXANDRIA_A, R.drawable.alexandria_a);
        civilisationMapper_.put(ALEXANDRIA_B, R.drawable.alexandria_b);
        civilisationMapper_.put(EPHESOS_A, R.drawable.ephesos_a);
        civilisationMapper_.put(EPHESOS_B, R.drawable.ephesos_b);
        civilisationMapper_.put(BABYLON_A, R.drawable.babylon_a);
        civilisationMapper_.put(BABYLON_B, R.drawable.babylon_b);
        civilisationMapper_.put(OLYMPIA_A, R.drawable.olympia_a);
        civilisationMapper_.put(OLYMPIA_B, R.drawable.olympia_b);
        civilisationMapper_.put(HALIKARNASSOS_A, R.drawable.halikarnassos_a);
        civilisationMapper_.put(HALIKARNASSOS_B, R.drawable.halikarnassos_b);
        civilisationMapper_.put(GIZAH_A, R.drawable.gizah_a);
        civilisationMapper_.put(GIZAH_B, R.drawable.gizah_b);
	}
}
