package ca.polymtl.inf8405.sevenwonders.controller;

import ca.polymtl.inf8405.sevenwonders.model.*;

import java.util.*;

public class PlayerManager {
	private Player me_;
	private int currentId_ = -1;
	private List<Player> allPlayers_;
	
	public PlayerManager(Player me, List<Player> allPlayers){
		me_ = me;
		allPlayers_ = allPlayers;
	}

	public PlayerManager(){
		testStub();
	}
	
	private void testStub(){
		//Create list of cards
		List<String> cards = new ArrayList<String>();
		for (int i = 0; i < 7; i++){
			cards.add(i+"");
		}
		
		// Create militaryCard
		List<String> miliCards = new ArrayList<String>();
		for (int i = 0 ; i < 3; i++){
			miliCards.add(i+"");
		}
		
		// Create science card
		List<String> scienceCards = new ArrayList<String>();
		for (int i = 0 ; i < 5; i++){
			scienceCards.add(i+"");
		}
		
		// Create played cards
		HashMap<String, List<String>> cardsOnBoard1 = new HashMap<String, List<String>>();
		cardsOnBoard1.put("military", miliCards);
		cardsOnBoard1.put("science", scienceCards);
		
		HashMap<String, List<String>> cardsOnBoard2 = new HashMap<String, List<String>>();
		cardsOnBoard2.put("culture", miliCards);
		cardsOnBoard2.put("trade", miliCards);
		
		HashMap<String, List<String>> cardsOnBoard3 = new HashMap<String, List<String>>();
		cardsOnBoard3.put("culture", miliCards);
		cardsOnBoard3.put("gill", miliCards);
		
		// Create players
		Player p1 = new Player(null, 3, cardsOnBoard1, "civi1");
		Player p2 = new Player(null, 3, cardsOnBoard2, "civi2");
		me_ = new Player(cards, 3, cardsOnBoard3, "civi3");
		
		allPlayers_ = new ArrayList<Player>();
		allPlayers_.add(p1);
		allPlayers_.add(me_);
		allPlayers_.add(p2);
	}
	
	public Player getPlayer(int id){
		return allPlayers_.get(id); 
	}

	public Player left(){
		if (currentId_ > 0)
			currentId_--;
		return allPlayers_.get(currentId_);
	}
	
	public Player right(){
		if (currentId_ < (allPlayers_.size() - 1))
			currentId_ ++;
		return allPlayers_.get(currentId_);
	}
	
	public Player getMe(){ return me_; }
	
	public int getMyId(){
		for (int i = 0; i < allPlayers_.size(); i++)
			if (allPlayers_.get(i).equals(me_) )
				return i;
		return 0;		
	}
	
	public void setPlayers(List<Player> allPlayers){ allPlayers_ = allPlayers; }
	
	public List<Player> getPlayers(){ return allPlayers_; }
	
	public int getCurrentId(){
		if (currentId_ < 0){
			currentId_ = getMyId();
		}
		return currentId_;
	}
	
	public void play(String cardName){
		me_.play(cardName);
	}
}
