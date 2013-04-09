package ca.polymtl.inf8405.sevenwonders.model;

import java.util.*;

import android.util.Log;


public class Player {
	public static final String[] CardCategory = {
		"military", "science", "culture", "gill", "trade", 
		"basicRessource", "advancedRessource"
	};

	private int coins_;
	private Set<Integer> battleMarkers_; // Temporary
	private HashMap<String, List<String>> played_;
	private String civilization_;

	public Player(int coins, HashMap<String, List<String>> played, String civilization){
		coins_ = coins;
		played_ = played;
		civilization_ = civilization;
	}

	public List<String> getPlayedCards(String category){ return played_.get(category); }

	public int getCoins(){ return coins_; }

	public String getCivilization() { return civilization_; }

	public void setPlayed(String category, List<String> played){ played_.put(category, played);}

	public void setCoins(int coins) { coins_ = coins; }

	public void setCivilizatioin(String civilization) { civilization_ = civilization; }

}
