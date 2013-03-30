package ca.polymtl.inf8405.sevenwonders;

import java.util.*;


import ca.polymtl.inf8405.sevenwonders.controller.CardLoader;
import ca.polymtl.inf8405.sevenwonders.model.Card;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

public class ResourceView extends View{

	private ResourceView sefl_ = this;
	private float CARD_RATIO = 1;
	private HashMap<String, Bitmap> cards_;

	private void init(Context context){
		setBackgroundColor(Color.GREEN);
		cards_ = new HashMap<String, Bitmap>();

		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent evt) {
				if (cards_.size() > 0){
					// Get all bitmap values
					List<String> cardNames = new ArrayList<String>();
					for (Object o: cards_.keySet().toArray())
						cardNames.add((String)o);

					GameScreen.showZoomPopup(sefl_,0, cardNames, false);
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

	public void addCard(String card){
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

	public void setCards( List<String> cards){
		if (cards != null){
			cards_.clear();
			for (String card: cards){
				addCard(card);
			}
		}
	}

}
