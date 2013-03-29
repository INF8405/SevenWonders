package ca.polymtl.inf8405.sevenwonders.model;

import java.util.*;

import android.util.Log;


public class Player {
	public static final String[] CardCategory = {
		"military", "science", "culture", "gill", "trade", 
		"basicRessource", "advancedRessource"
	};

	private List<String> hand_;
	private int coins_;
	private Set<Integer> battleMarkers_; // Temporary
	private HashMap<String, List<String>> played_;
	private String civilization_;

	public Player(List<String> hand, int coins, HashMap<String, List<String>> played, String civilization){
		hand_ = hand;
		coins_ = coins;
		played_ = played;
		civilization_ = civilization;
	}

	public List<String> getHand(){ return hand_; }

	public List<String> getPlayedCards(String category){ return played_.get(category); }

	public int getCoins(){ return coins_; }

	public String getCivilization() { return civilization_; }

	public void setHand(List<String> hand){ hand_ = hand; }

	public void setPlayed(String category, List<String> played){ played_.put(category, played);}

	public void setCoins(int coins) { coins_ = coins; }

	public void setCivilizatioin(String civilization) { civilization_ = civilization; }

	public void play(String cardName){
		// Remove the card from hand_
		int i = hand_.indexOf(cardName);
		Log.e(this.getClass().toString(), "Card=" + cardName + " - index = " + i);
		if (i != -1 ) // I dont understand why this function is called twice after each touch
			// Please Help me!!
			hand_.remove(i); 
		
		// Add the card to played_ - TEMP - Remove in integration - API will take care of this
		
	} 

}
