package ca.polymtl.inf8405.sevenwonders;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import ca.polymtl.inf8405.sevenwonders.controller.CardLoader;
import ca.polymtl.inf8405.sevenwonders.model.Card;

import java.util.*;

public class PlayedCards extends View {
	private HashMap<String, Bitmap> cards_;
	private static float CARD_WIDTH = 0;
	private static float CARD_HEIGHT = 0;
	private static int MARGIN_LEFT = 0;
	private static int MARGIN_TOP = 0;
	private PlayedCards sefl_ = this;

	private void init(Context context){
		cards_ = new HashMap<String, Bitmap>();

		// Calcul card size and margin value based on the screen dimensions
		int screenWidth = ((WindowManager)context
				.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay().getWidth();
		CARD_WIDTH = screenWidth / 8;
		MARGIN_LEFT = (int)CARD_WIDTH / 6;

		Bitmap cardBitmap = CardLoader.getInstance().getBitmap(getContext(), "0");
		CARD_HEIGHT = CARD_WIDTH * cardBitmap.getHeight() / cardBitmap.getWidth();
		MARGIN_TOP = (int)CARD_HEIGHT / 6;

		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if (cards_.size() > 0){
					// Get all bitmap values
					List<String> cardNames = new ArrayList<String>();
					for (Object o: cards_.keySet().toArray())
						cardNames.add((String)o);

					GameScreen.showZoomPopup(sefl_, cardNames, getContext());
				}

				return false;
			}
		});
	}

	public PlayedCards(Context context) {
		super(context);
		init(context);
	}

	public PlayedCards(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	@Override
	public void onDraw(Canvas canvas) {
		int top = 0;
		int left = 0;
		for(Object object: cards_.keySet().toArray()){
			String cardName = (String)object;
			canvas.drawBitmap(cards_.get(cardName), left, top, null);
			top += MARGIN_TOP;
			left += MARGIN_LEFT;
		}
	}

	public void addCard( Card card ){
		if (cards_ == null)
			cards_ = new HashMap<String, Bitmap>();
		Bitmap resizedBitmap = Bitmap.createScaledBitmap(
				CardLoader.getInstance().getBitmap(getContext(), card.getName()), 
				(int)CARD_WIDTH, 
				(int)CARD_HEIGHT, 
				false);
		cards_.put(card.getName(), resizedBitmap);
	}
}
