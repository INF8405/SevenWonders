package ca.polymtl.inf8405.sevenwonders;

import java.util.*;


import ca.polymtl.inf8405.sevenwonders.api.Card;
import ca.polymtl.inf8405.sevenwonders.controller.CardLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ResourceView extends View implements CardView {

	private ResourceView sefl_ = this;
	private float CARD_RATIO = 1;
	private HashMap<Card, Bitmap> cards_;

	private void init(Context context){
		setBackgroundColor(Color.GREEN);
		cards_ = new HashMap<Card, Bitmap>();

		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent evt) {
				if (cards_.size() > 0){
					// Get all bitmap values
					List<Card> cards = new ArrayList<Card>();
					for( Map.Entry<Card,Bitmap> entry : cards_.entrySet() ) {
                        cards.add(entry.getKey());
                    }

					GameScreenActivity.showZoomPopup(sefl_, 0, cards, false);
				}
				return false;
			}
		});
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

		for(Object object: cards_.keySet().toArray()){
			String cardName = (String)object;
			canvas.drawBitmap(cards_.get(cardName), 0, topCorner, null);
			topCorner += headerSize;
		}
	}

	public void addCard(Card card){
		Bitmap cardBm;
		if ( (getHeight() == 0) || (getWidth() == 0) ){
			cardBm = CardLoader.getInstance().getBitmap(getContext(), card);
		}
		else{
			CARD_RATIO =  getHeight() / getWidth();
			float cardHeight = CARD_RATIO * getWidth();
			cardBm = Bitmap.createScaledBitmap(
					CardLoader.getInstance().getBitmap(getContext(), card), 
					getWidth(), 
					(int)cardHeight, false);
		}
		cards_.put(card, cardBm);
	}

	public void setCards( List<Card> cards){
		if (cards != null){
			cards_.clear();
			for (Card card: cards){
				addCard(card);
			}
		}
	}

}
