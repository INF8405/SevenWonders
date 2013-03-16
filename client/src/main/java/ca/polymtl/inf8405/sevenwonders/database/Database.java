package ca.polymtl.inf8405.sevenwonders.database;

import java.util.*;

import ca.polymtl.inf8405.sevenwonders.R;

public class Database {
	private static HashMap<String, Integer> cardMapper_;
	private static Database instance_;
	
	private Database(){
		cardMapper_ = new HashMap<String, Integer>();
		// Setup all cards here
		cardMapper_.put("0", R.drawable.green_card);
		cardMapper_.put("1", R.drawable.green_card);
		cardMapper_.put("2", R.drawable.green_card);
		cardMapper_.put("3", R.drawable.green_card);
		cardMapper_.put("4", R.drawable.green_card);
		cardMapper_.put("5", R.drawable.green_card);
		cardMapper_.put("6", R.drawable.green_card);
		
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

}
