package ca.polymtl.inf8405.sevenwonders.database;

import java.util.*;

import android.content.Context;
import android.util.Log;

import ca.polymtl.inf8405.sevenwonders.R;

public class Database {
	private static Map<String, Integer> cardMapper_;
	private static Map<String, Integer> civilisationMapper_;
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

	public Integer getBitmapId(String cardName){
		if (cardMapper_.containsKey(cardName))
			return cardMapper_.get(cardName);
		else
			return 0;
	}
	
	public Integer getCivilisationBitmapId(String civilisationName){
		if (civilisationMapper_.containsKey(civilisationName))
			return civilisationMapper_.get(civilisationName);
		else
			return 0;
	}
	
	private static void setupCardMapper(){
		cardMapper_ = new HashMap<String, Integer>();
		// Setup all cards here
		cardMapper_.put("0", R.drawable.altar);
		cardMapper_.put("1", R.drawable.apothecary);
		cardMapper_.put("2", R.drawable.baracks);
		cardMapper_.put("3", R.drawable.baths);
		cardMapper_.put("4", R.drawable.clay_pit);
		cardMapper_.put("5", R.drawable.clay_pool);
		cardMapper_.put("6", R.drawable.east_trading_port);
		cardMapper_.put("7", R.drawable.excavation);
		cardMapper_.put("8", R.drawable.guard_tower);
		cardMapper_.put("9", R.drawable.loom);
		cardMapper_.put("10", R.drawable.lumber_yard);
		cardMapper_.put("11", R.drawable.marketplace);
		cardMapper_.put("12", R.drawable.ore_vein);
		cardMapper_.put("13", R.drawable.pawnshop);
		cardMapper_.put("14", R.drawable.scriptorium);
		cardMapper_.put("15", R.drawable.stockade);
		cardMapper_.put("16", R.drawable.stone_pit);
		cardMapper_.put("17", R.drawable.tavern);
		cardMapper_.put("18", R.drawable.theater);
		cardMapper_.put("19", R.drawable.timber_yard);
		cardMapper_.put("20", R.drawable.tree_farm);
		cardMapper_.put("21", R.drawable.west_trading_post);
		cardMapper_.put("22", R.drawable.workshop);
	}

	private static void setupCivilisation(){
		civilisationMapper_ = new HashMap<String, Integer>();
		civilisationMapper_.put("0", R.drawable.alexandria_a);
		civilisationMapper_.put("1", R.drawable.alexandria_b);
		civilisationMapper_.put("2", R.drawable.babylon_a);
		civilisationMapper_.put("3", R.drawable.babylon_b);
		civilisationMapper_.put("4", R.drawable.ephesos_a);
		civilisationMapper_.put("5", R.drawable.ephesos_b);
		civilisationMapper_.put("6", R.drawable.halikarnassos_a);
		civilisationMapper_.put("7", R.drawable.halikarnassos_b);
		civilisationMapper_.put("8", R.drawable.olympia_a);
		civilisationMapper_.put("9", R.drawable.olympia_b);
		civilisationMapper_.put("10", R.drawable.rhodos_a);
		civilisationMapper_.put("11", R.drawable.rhodos_b);
	}
}
