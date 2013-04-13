package ca.polymtl.inf8405.sevenwonders;

import ca.polymtl.inf8405.sevenwonders.api.Card;
import ca.polymtl.inf8405.sevenwonders.api.Civilisation;
import ca.polymtl.inf8405.sevenwonders.controller.CardLoader;
import ca.polymtl.inf8405.sevenwonders.database.Database;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.*;

public class PlayerStateView extends View{

	private HashMap<Card, Bitmap> cardsInHand_;
	private Civilisation civilisation_;
	private static float cardWidth_ = 0;
	private static float cardHeight_ = 0;
	private View seft_ = this;

	private void init(Context context){
		setBackgroundResource(R.drawable.seven_wonders_bg);
		cardsInHand_ = new HashMap<Card, Bitmap>();
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent evt) {
				// Get all card names
				List<Card> cards = new ArrayList<Card>();
				for( Map.Entry<Card,Bitmap> entry : cardsInHand_.entrySet() ) {
                    cards.add(entry.getKey());
                }

				int selectedCardId = findSelectedCard(evt.getX(), evt.getY());
				if (selectedCardId != -1)
					GameScreenActivity.showZoomPopup(seft_, selectedCardId, cards, true);
				return false;
			}
		});
	}

	public PlayerStateView(Context context) {
		super(context);
		init(context);
	}

	public PlayerStateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@Override
	public void onDraw(Canvas canvas) {
		invalidate();

		// Calcul top, left
		int top = getHeight() / 6; // 40
		int left = 0;

        for( Map.Entry<Card,Bitmap> entry : cardsInHand_.entrySet() ) {
            canvas.drawBitmap(entry.getValue(), left, top, null);
            left += (int)cardWidth_;
		}
	}

	public void setCardSize(float viewHeight){
		Bitmap cardSample = CardLoader.getInstance().getBitmap(getContext(), Card.TAVERN); // Fixme: ???
		cardHeight_ = viewHeight * 2 / 3;
		cardWidth_ = cardHeight_ * cardSample.getWidth() / cardSample.getHeight();
	}

	public void setCards(List<Card> cards){
		Bitmap cardBm;
		for (Card card: cards){
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
	
	public void setCivilisation(Civilisation civilisation){
		invalidate();
		civilisation_ = civilisation;
		setBackgroundResource(Database.getInstance().getCivilisationBitmapId(civilisation_));
	}

	private int findSelectedCard(float x, float y){
		for(int i = 0 ; i < cardsInHand_.size(); i++){
			if ( (i*cardWidth_ < x) && (x < (i+1)*cardWidth_))
				return i;
		}
		return -1;
	}
}
