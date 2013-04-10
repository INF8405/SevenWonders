package ca.polymtl.inf8405.sevenwonders.controller;

import android.util.Log;
import ca.polymtl.inf8405.sevenwonders.model.*;

import java.util.*;

public class PlayerManager {
	private Player me_;
	private List<String> hand_;
	private int currentId_ = -1;
	private List<Player> allPlayers_;

	public PlayerManager(Player me, List<Player> allPlayers, List<String> hand){
		me_ = me;
		hand_ = hand;
		allPlayers_ = allPlayers;
	}

	public PlayerManager(){
		testStub();
	}

	public List<String> getHand(){ return hand_; }

	public void setHand(List<String> hand){ hand_ = hand; }

	private void testStub(){
//		//Create list of cards
//		List<String> cards = new ArrayList<String>();
//		for (int i = 0; i < 22; i++){
//			cards.add(i+"");
//		}

		// Create militaryCard
		List<String> miliCards = new ArrayList<String>();
		for (int i = 0 ; i < 5; i++){
			miliCards.add(i+"");
		}

		// Create science card
		List<String> scienceCards = new ArrayList<String>();
		for (int i = 6 ; i < 10; i++){
			scienceCards.add(i+"");
		}

		// Create trade card
		List<String> tradeCards = new ArrayList<String>();
		for (int i = 11 ; i < 15; i++){
			tradeCards.add(i+"");
		}
		// Create culture card
		List<String> cultureCards = new ArrayList<String>();
		for (int i = 16 ; i < 20; i++){
			cultureCards.add(i+"");
		}
		
		// Create played cards
		HashMap<String, List<String>> cardsOnBoard1 = new HashMap<String, List<String>>();
		cardsOnBoard1.put("military", miliCards);
		cardsOnBoard1.put("science", scienceCards);
		cardsOnBoard1.put("trade", tradeCards);

		HashMap<String, List<String>> cardsOnBoard2 = new HashMap<String, List<String>>();
		cardsOnBoard2.put("culture", cultureCards);
		cardsOnBoard2.put("trade", tradeCards);
		cardsOnBoard2.put("basicRessource", miliCards);

		HashMap<String, List<String>> cardsOnBoard3 = new HashMap<String, List<String>>();
		cardsOnBoard3.put("culture", cultureCards);
		cardsOnBoard3.put("gill", miliCards);
		cardsOnBoard3.put("advancedRessource", tradeCards);

		// Create cards in player hand
		hand_ = new ArrayList<String>();
		for (int i = 0 ; i < 7; i++){
			// Generate random number : Min + (int)(Math.random() * ((Max - Min) + 1))
			int random = 0 + (int)(Math.random() * ((22 - 0) + 1));
			hand_.add(""+random);
		}
		// Create players
		Player p1 = new Player(3, cardsOnBoard1, "civi1");
		Player p2 = new Player(3, cardsOnBoard2, "civi2");
		me_ = new Player(3, cardsOnBoard3, "civi3");

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
		// Remove the card from hand_
		int i = hand_.indexOf(cardName);
		Log.e(this.getClass().toString(), "Card=" + cardName + " - index = " + i);
		if (i != -1 ) // I dont understand why this function is called twice after each touch			
			// Please Help me!!

			// => BECAUSE YOUR FONCTION ONTOUCHELISTENER WILL BE CALLED EVERYTIME IT RECEIVES A TOUCH_DOWN
			// OR TOUCH_UP. YOU NEED TO PUSH AN IF (TOUCHE_DOWN) INSIDE OF THE CALLER, REMOVE THIS STUPID IF HERE
			// PLEASE DO IT!!!!!
			hand_.remove(i); 

		// Add the card to played_ - TEMP - Remove in integration - API will take care of this

	}
}
