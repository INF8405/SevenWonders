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
		List<Card> cards = new ArrayList<Card>();
		for (int i = 0; i < 7; i++){
			Card card = new Card(i+"");
			cards.add(card);
		}
		
		// Create militaryCard
		List<MilitaryCard> miliCards = new ArrayList<MilitaryCard>();
		for (int i = 0 ; i < 3; i++){
			miliCards.add(new MilitaryCard(i+""));
		}
		
		// Create science card
		List<ScienceCard> scienceCards = new ArrayList<ScienceCard>();
		for (int i = 0 ; i < 5; i++){
			scienceCards.add(new ScienceCard(i+""));
		}
		
		// Create played cards
		List<Card> played1 = new ArrayList<Card>(miliCards);
		played1.add(scienceCards.get(0));
		played1.add(scienceCards.get(1));
		
		List<Card> played2 = new ArrayList<Card>(scienceCards);
		played2.add(miliCards.get(1));
		played2.add(miliCards.get(2));
		
		// Create players
		Player p1 = new Player(null, 3, played1, "civi1");
		Player p2 = new Player(null, 3, played2, "civi2");
		me_ = new Player(cards, 3, played2, "civi3");
		
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
}
