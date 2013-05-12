package ca.polymtl.inf8405.sevenwonders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import ca.polymtl.inf8405.sevenwonders.api.Card;
import ca.polymtl.inf8405.sevenwonders.api.CardCategory;
import ca.polymtl.inf8405.sevenwonders.controller.CardLoader;
import ca.polymtl.inf8405.sevenwonders.model.*;

import java.util.*;

import com.google.android.gms.R.color;

public class ResourceView extends CardView {

	private ResourceView sefl_ = this;
	private float CARD_RATIO = 1;
	private static float CARD_WIDTH = 0;
	private static float CARD_HEIGHT = 0;

	private void init(Context context){
		//setBackgroundResource(color.ressourceBg);

		// Calcul card size and margin value based on the screen dimensions
		int screenWidth = ((WindowManager)context
				.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay().getWidth();
		CARD_WIDTH = screenWidth / 8;

		Bitmap cardBitmap = CardLoader.getInstance().getBitmap(getContext(), Card.ALTAR); // FIXME:: duc
		CARD_HEIGHT = CARD_WIDTH * cardBitmap.getHeight() / cardBitmap.getWidth();
	}

	public ResourceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public ResourceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	@Override
	public void onDraw(Canvas canvas) {
		invalidate();

		// Calcul header size
		CARD_RATIO =  getHeight() / getWidth();
		float cardHeight = CARD_RATIO * getWidth(); 
		int topCorner = 0;
		int headerSize = (int)((getHeight() - cardHeight)/(cards_.size() - 1));

		for (Map.Entry<CardInfo,Bitmap> entry : cards_.entrySet()){
			canvas.drawBitmap(entry.getValue(), 0, topCorner,null);
			topCorner += headerSize;
		}

		//		for(Object object: cards_.keySet().toArray()){
		//			String cardName = (String)object;
		//			canvas.drawBitmap(cards_.get(cardName), 0, topCorner, null);
		//			topCorner += headerSize;
		//		}
	}

	@Override
	public void addCard(CardInfo card){
		Bitmap resizedBitmap = Bitmap.createScaledBitmap(
				CardLoader.getInstance().getBitmap(getContext(), card.getName()), 
				(int)CARD_WIDTH, 
				(int)CARD_HEIGHT, 
				false);
		
//		Bitmap cardBm;
//		if ( (getHeight() == 0) || (getWidth() == 0) ){
//			cardBm = CardLoader.getInstance().getBitmap(getContext(), card.getName());
//		}
//		else{
//			CARD_RATIO =  getHeight() / getWidth();
//			float cardHeight = CARD_RATIO * getWidth();
//			cardBm = Bitmap.createScaledBitmap(
//					CardLoader.getInstance().getBitmap(getContext(), card.getName()), 
//					getWidth(), 
//					(int)cardHeight, false);
//		}
		cards_.put(card, resizedBitmap);
	}

	@Override
	public int findSelectedCard(float x, float y){
		return 0; // Fixme : Write function to detect selected card
	}

}
