package ca.polymtl.inf8405.sevenwonders;

import ca.polymtl.inf8405.sevenwonders.R;
import ca.polymtl.inf8405.sevenwonders.controller.CardLoader;
import ca.polymtl.inf8405.sevenwonders.model.*;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
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
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
//				if (cardsInHand_.size() > 0)
//					play((Card)cardsInHand_.keySet().toArray()[0]);
				
				// Get all card names
				List<String> cardNames = new ArrayList<String>();
				for (Object o: cardsInHand_.keySet().toArray())
					cardNames.add((String)o);
				
				GameScreen.showZoomPopup(seft_, cardNames, getContext());
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
	
	public void setCards(List<Card> cards){
		Bitmap cardBm;
		for (Card card: cards){
			if ((cardHeight_ == 0) && (cardWidth_ == 0)){
				cardBm = CardLoader.getInstance().getBitmap(getContext(), card.getName());
			}
			else{
				// Resize Bitmap
				cardBm = Bitmap.createScaledBitmap(
						CardLoader.getInstance().getBitmap(getContext(), card.getName()),
						(int)cardWidth_, (int)cardHeight_, false);
			}
			cardsInHand_.put(card.getName(), cardBm );
		}
	}

	public void play(Card card){
		cardsInHand_.remove(card.getName());
	}

	/*	@Override
	protected  void onMeasure(int widthSpec, int heightSpec){
		// Assurer que le canvas est toujours un carre
		int measuredWidth = MeasureSpec.getSize(widthSpec);
		int measureHeigth = MeasureSpec.getSize(heightSpec);

		int d = Math.min(measuredWidth, measureHeigth);
		setMeasuredDimension(measuredWidth, d * 1/3);
	}
	 */
}
