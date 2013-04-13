package ca.polymtl.inf8405.sevenwonders.controller;

import java.util.*;

import ca.polymtl.inf8405.sevenwonders.R;
import ca.polymtl.inf8405.sevenwonders.api.Card;
import ca.polymtl.inf8405.sevenwonders.database.Database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CardLoader {
	private static HashMap<Card, Bitmap> cardDataBase_ = new HashMap<Card, Bitmap>();
	private static CardLoader instance_;
	
	private CardLoader(){
		
	}
	
	public static CardLoader getInstance(){
		if (instance_ == null)
			instance_ = new CardLoader();
		return instance_;
	}
	
	public Bitmap getBitmap(Context context, Card card){
		if (cardDataBase_.containsKey(card))
			return cardDataBase_.get(card);
		else {
			Bitmap cardBm = BitmapFactory.decodeResource(context.getResources(), 
					Database.getInstance().getBitmapId(card));
			cardDataBase_.put(card, cardBm);
			return cardBm;
		}
	}

}
