package ca.polymtl.inf8405.sevenwonders.model;

import ca.polymtl.inf8405.sevenwonders.api.Card;
import ca.polymtl.inf8405.sevenwonders.api.Resource;
import ca.polymtl.inf8405.sevenwonders.api.NeighborReference;

import java.util.*;

public class CardInfo {
	private Card cardName_;
	private Set<Map<Resource,List<NeighborReference>>> trades_ = 
			new HashSet<Map<Resource,List<NeighborReference>>>();
	private boolean playable_=false;
	
	public CardInfo(Card cardName){
		cardName_ = cardName;
	}
	
	public CardInfo(Card cardName, boolean playable, Set<Map<Resource,List<NeighborReference>>> trades){
		cardName_ = cardName;
		playable_ = playable;
		trades_ = trades;
	}
	
	public Card getName(){
		return cardName_;
	}
	
	public void setName(Card cardName){
		cardName_ = cardName;
	}
	
	public boolean isPlayable() { return playable_; }
	public Set<Map<Resource,List<NeighborReference>>> getTrades(){ return trades_; }
}
