package ca.polymtl.inf8405.sevenwonders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import ca.polymtl.inf8405.sevenwonders.controller.CardLoader;
import ca.polymtl.inf8405.sevenwonders.model.*;

import java.util.*;

public class ResourceView extends View implements CardView {

	private ResourceView sefl_ = this;
	private float CARD_RATIO = 1;
	private HashMap<CardInfo, Bitmap> cards_;

	private void init(Context context){
		setBackgroundColor(Color.GREEN);
		cards_ = new HashMap<CardInfo, Bitmap>();

		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent evt) {
				if (cards_.size() > 0){
					// Get all bitmap values
					List<CardInfo> cards = new ArrayList<CardInfo>();
					for( Map.Entry<CardInfo,Bitmap> entry : cards_.entrySet() ) {
                        cards.add(entry.getKey());
                    }

			
					GameScreenActivity.showZoomPopup(sefl_, 0, cards, false,false);
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

	public void addCard(CardInfo card){
		Bitmap cardBm;
		if ( (getHeight() == 0) || (getWidth() == 0) ){
			cardBm = CardLoader.getInstance().getBitmap(getContext(), card.getName());
		}
		else{
			CARD_RATIO =  getHeight() / getWidth();
			float cardHeight = CARD_RATIO * getWidth();
			cardBm = Bitmap.createScaledBitmap(
					CardLoader.getInstance().getBitmap(getContext(), card.getName()), 
					getWidth(), 
					(int)cardHeight, false);
		}
		cards_.put(card, cardBm);
	}

	public void setCards( List<CardInfo> cards){
		if (cards != null){
			cards_.clear();
			for (CardInfo card: cards){
				addCard(card);
			}
		}
	}

}
