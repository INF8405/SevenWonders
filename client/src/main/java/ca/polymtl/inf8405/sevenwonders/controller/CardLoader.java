package ca.polymtl.inf8405.sevenwonders.controller;

import java.util.*;

import ca.polymtl.inf8405.sevenwonders.R;
import ca.polymtl.inf8405.sevenwonders.api.Card;
import ca.polymtl.inf8405.sevenwonders.database.Database;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.util.Log;

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
			Bitmap cardBm;
			// Access to other application
			PackageManager pm = context.getPackageManager();
			try {
				Resources resources = pm.getResourcesForApplication("ca.polymtl.inf8405.sevenwondersassets");
				int id = resources.getIdentifier(card.toString().toLowerCase(), "drawable", 
						"ca.polymtl.inf8405.sevenwondersassets");
				if (id != 0){
					Drawable d = resources.getDrawable(id);
					cardBm = drawableToBitmap(d);
					cardDataBase_.put(card, cardBm);
					return cardBm;
				}
				else {
					Log.e("CardLoader 1:", "Cant find card: " + card.toString().toLowerCase()+ " - " + id);
					return BitmapFactory.decodeResource(context.getResources(), 
							R.drawable.ic_launcher);
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				Log.e("CardLoader", "Exception:" + e.getMessage());
			}
			return null;
		}
	}
	
	public static Bitmap drawableToBitmap( Drawable d) {
		Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), 
				d.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap); 
		d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		d.draw(canvas);
		return bitmap;
	}

}

/** This code will be usefull when merging assets with application ( put it in getBitmap() function)

if (cardDataBase_.containsKey(card))
	return cardDataBase_.get(card);
else {
	Bitmap cardBm = BitmapFactory.decodeResource(context.getResources(), 
			Database.getInstance().getBitmapId(card));
	cardDataBase_.put(card, cardBm);
	return cardBm;
}
**/
