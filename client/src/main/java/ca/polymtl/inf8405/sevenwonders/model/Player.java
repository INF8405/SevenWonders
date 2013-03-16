package ca.polymtl.inf8405.sevenwonders.model;

import java.util.*;

public class Player {
	private List<Card> hand_;
	private int coins_;
	private Set<Integer> battleMarkers_; // Temporary
	private List<Card> played_;
	private String civilization_;
	
	public Player(List<Card> hand, int coins, List<Card> played, String civilization){
		hand_ = hand;
		coins_ = coins;
		played_ = played;
		civilization_ = civilization;
	}
	
	public List<Card> getHand(){ return hand_; }
	
	public List<Card> getPlayed(){ return played_; }
	
	public int getCoins(){ return coins_; }
	
	public String getCivilization() { return civilization_; }
	
	public void setHand(List<Card> hand){ hand_ = hand; }
	
	public void setPlayed(List<Card> played){ played_ = played; }
	
	public void setCoins(int coins) { coins_ = coins; }
	
	public void setCivilizatioin(String civilization) { civilization_ = civilization; }
	
}
