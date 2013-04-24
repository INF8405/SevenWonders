package ca.polymtl.inf8405.sevenwonders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.WindowManager;

import ca.polymtl.inf8405.sevenwonders.api.Card;
import ca.polymtl.inf8405.sevenwonders.controller.CardLoader;
import ca.polymtl.inf8405.sevenwonders.model.*;

import java.util.*;

public class PlayedCards extends CardView {
	private static float CARD_WIDTH = 0;
	private static float CARD_HEIGHT = 0;
	private static int MARGIN_LEFT = 0;
	private static int MARGIN_TOP = 0;
	private PlayedCards self_ = this;

	private void init(Context context){
		// Calcul card size and margin value based on the screen dimensions
		int screenWidth = ((WindowManager)context
				.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay().getWidth();
		CARD_WIDTH = screenWidth / 8;
		MARGIN_LEFT = (int)CARD_WIDTH / 10;

		Bitmap cardBitmap = CardLoader.getInstance().getBitmap(getContext(), Card.ALTAR);
		CARD_HEIGHT = CARD_WIDTH * cardBitmap.getHeight() / cardBitmap.getWidth();
		MARGIN_TOP = (int)CARD_HEIGHT / 10;
	}

	public PlayedCards(Context context) {
		super(context);
		init(context);
	}

	public PlayedCards(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@Override
	public void onDraw(Canvas canvas) {
		this.invalidate();
		int top = 0;
		int left = 0;
		
		for (Map.Entry<CardInfo,Bitmap> entry : cards_.entrySet()){
			canvas.drawBitmap(entry.getValue(), left, top, null);
			top += MARGIN_TOP;
			left += MARGIN_LEFT;
		}
		
//		for(Object object: cards_.keySet().toArray()){
//			String cardName = (String)object;
//			canvas.drawBitmap(cards_.get(cardName), left, top, null);
//			top += MARGIN_TOP;
//			left += MARGIN_LEFT;
//		}
	}

	@Override
	public void addCard( CardInfo card ){
		if (cards_ == null)
			cards_ = new HashMap<CardInfo, Bitmap>();
		Bitmap resizedBitmap = Bitmap.createScaledBitmap(
				CardLoader.getInstance().getBitmap(getContext(), card.getName()), 
				(int)CARD_WIDTH, 
				(int)CARD_HEIGHT, 
				false);
		cards_.put(card, resizedBitmap);
	}

	@Override
	public int findSelectedCard(float x, float y){
		for(int i = cards_.size() ; i > 0 ; i--){
			if ( (i*MARGIN_LEFT < x) && (x < (i*MARGIN_LEFT+CARD_WIDTH)) 
					&& ((i-1)*MARGIN_TOP < y) && (y < ((i-1)*MARGIN_TOP+CARD_HEIGHT)) )
				return i-1;
		}
		return -1;
	}
}
