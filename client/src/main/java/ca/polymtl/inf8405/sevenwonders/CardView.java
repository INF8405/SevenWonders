package ca.polymtl.inf8405.sevenwonders;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import ca.polymtl.inf8405.sevenwonders.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardView extends View{
	protected HashMap<CardInfo, Bitmap> cards_ = new HashMap<CardInfo, Bitmap>();
	private CardView self_ = this;

	private void setListener(){
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent evt) {
				// TODO Auto-generated method stub
				if (cards_.size() > 0){
					// Get all bitmap key
					List<CardInfo> cards = new ArrayList<CardInfo>();
					for( Map.Entry<CardInfo,Bitmap> entry : cards_.entrySet() ) {
						cards.add(entry.getKey());
					}

					int selectedCardId = findSelectedCard(evt.getX(), evt.getY());
					if (selectedCardId != -1)
						GameScreenActivity.showZoomPopup(self_, selectedCardId, cards, false, false);
				}
				return false;
			}
		});
	}
	
	public CardView(Context context) {
		super(context);
		setListener();
	}

	public CardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setListener();
	}

	public void addCard(CardInfo card){ } //The child must implement this method

	public void setCards( List<CardInfo> cards){
		if (cards != null){
			cards_.clear();
			for (CardInfo card: cards){
				addCard(card);
			}
		}
	}

	public int findSelectedCard(float x, float y){
		// Child must implement this methods
		return -1;
	}
}