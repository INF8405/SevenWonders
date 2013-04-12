package ca.polymtl.inf8405.sevenwonders;

import ca.polymtl.inf8405.sevenwonders.api.Player;
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

	private HashMap<String, Bitmap> cardsInHand_;
	private Player player_;
	private static float cardWidth_ = 0;
	private static float cardHeight_ = 0;
	private View self_ = this;

	private void init(Context context){
		// Image by default
		setBackgroundResource(R.drawable.seven_wonders_bg);
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
					GameScreenActivity.showZoomPopup(self_, selectedCardId, cardNames, true, 
							player_.canPlayWonder);
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
	
	public void setPlayer(Player player){
		invalidate();
		player_ = player;
		setBackgroundResource(Database.getInstance().getCivilisationBitmapId(player_.civilisation));
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
