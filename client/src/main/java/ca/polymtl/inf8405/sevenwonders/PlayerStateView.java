package ca.polymtl.inf8405.sevenwonders;

import ca.polymtl.inf8405.sevenwonders.R;
import ca.polymtl.inf8405.sevenwonders.controller.CardLoader;
import ca.polymtl.inf8405.sevenwonders.model.*;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import java.util.*;

public class PlayerStateView extends View{

	private HashMap<String, Bitmap> cardsInHand_;
	private static float cardWidth_ = 0;
	private static float cardHeight_ = 0;
	private View seft_ = this;
	
	private void init(Context context){
		setBackgroundResource(R.drawable.civilization1);
		cardsInHand_ = new HashMap<String, Bitmap>();
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent evt) {
				// Get all card names
				List<String> cardNames = new ArrayList<String>();
				Object[] allCards = cardsInHand_.keySet().toArray(); 
				for (int i = 0 ; i < allCards.length; i++){
					cardNames.add((String)allCards[i]);
				}
				
				int selectedCardId = findSelectedCard(evt.getX(), evt.getY());
				if (selectedCardId != -1)
					GameScreen.showZoomPopup(seft_, selectedCardId, cardNames, getContext(), true);
				return false;
			}
		});
	}

	public PlayerStateView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public PlayerStateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	@Override
	public void onDraw(Canvas canvas) {
		invalidate();

		// Calcul top, left
		int top = getHeight() / 6; // 40
		int left = 0;

		for(Object object: cardsInHand_.keySet().toArray()){
			String cardName = (String)object;
			if (cardsInHand_.containsKey(cardName)){
				canvas.drawBitmap(cardsInHand_.get(cardName), left, top, null);
				left += (int)cardWidth_;
			}
		}
	}

	public void setCardSize(float viewHeight){
		Bitmap cardSample = CardLoader.getInstance().getBitmap(getContext(), "0"); 
		cardHeight_ = viewHeight * 2 / 3;
		cardWidth_ = cardHeight_ * cardSample.getWidth() / cardSample.getHeight();
	}
	
	public void setCards(List<String> cards){
		Bitmap cardBm;
		for (String card: cards){
			if ((cardHeight_ == 0) && (cardWidth_ == 0)){
				cardBm = CardLoader.getInstance().getBitmap(getContext(), card);
			}
			else{
				// Resize Bitmap
				cardBm = Bitmap.createScaledBitmap(
						CardLoader.getInstance().getBitmap(getContext(), card),
						(int)cardWidth_, (int)cardHeight_, false);
			}
			cardsInHand_.put(card, cardBm );
		}
	}

	public void play(String cardName){
		cardsInHand_.remove(cardName);
	}

	private int findSelectedCard(float x, float y){
		for(int i = 0 ; i < cardsInHand_.size(); i++){
			if ( (i*cardWidth_ < x) && (x < (i+1)*cardWidth_))
				return i;
		}
		return -1;
	}
	
}
