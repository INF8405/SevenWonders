package ca.polymtl.inf8405.sevenwonders.controller;

import java.util.*;

import ca.polymtl.inf8405.sevenwonders.R;
import ca.polymtl.inf8405.sevenwonders.database.Database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CardLoader {
	private static HashMap<String, Bitmap> cardDataBase_ = new HashMap<String, Bitmap>();
	private static CardLoader instance_;
	
	private CardLoader(){
		
	}
	
	public static CardLoader getInstance(){
		if (instance_ == null)
			instance_ = new CardLoader();
		return instance_;
	}
	
	public Bitmap getBitmap(Context context, String cardName){
		if (cardDataBase_.containsKey(cardName))
			return cardDataBase_.get(cardName);
		else {
			Bitmap cardBm = BitmapFactory.decodeResource(context.getResources(), 
					Database.getInstance().getBitmapId(cardName));
			cardDataBase_.put(cardName, cardBm);
			return cardBm;
		}
	}

}
