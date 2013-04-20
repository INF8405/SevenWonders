package ca.polymtl.inf8405.sevenwonders;

import ca.polymtl.inf8405.sevenwonders.api.NeighborReference;
import ca.polymtl.inf8405.sevenwonders.api.Resource;
import ca.polymtl.inf8405.sevenwonders.controller.CardLoader;
import ca.polymtl.inf8405.sevenwonders.controller.OnFlingGestureListener;
import ca.polymtl.inf8405.sevenwonders.model.*;

import android.app.FragmentManager;

import android.content.Context;
import android.graphics.Color;
//import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.*;

public class ZoomCardView extends RelativeLayout{
	private View sefl_ = this;
	private OnFlingGestureListener flingGesture_;
	private List<CardInfo> allCards_;
	private int current_; // current card id

	// Test - TO REMOVE
	private TextView text;
	private void changeText(){
		text.setText("Index=" + current_);
	}

	/**
	 * Build the zoom view
	 * @param context
	 * @param cards: list of all cards will be displayed in the zoom view
	 * @param current: the id of the current card
	 * @param withButtonPanel: if we want to display the button panel inside of the zoom view
	 */
	private void init(Context context, List<CardInfo> cards, int current, boolean withButtonPanel,boolean canPlayWonder){
		current_ = current;
		allCards_ = cards;

		setBackgroundColor(Color.DKGRAY);
		setGravity(Gravity.RIGHT);

		// Image
		ImageView img = new ImageView(context);
		LinearLayout.LayoutParams imgLayout = new LinearLayout.LayoutParams
				(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		imgLayout.gravity = Gravity.CENTER;
		img.setLayoutParams(imgLayout);
		img.setImageBitmap(CardLoader.getInstance()
				.getBitmap(context, allCards_.get(current_).getName()));
		img.setTag("imageView");
		addView(img);

		// Test textview - TO REMOVE
		text = new TextView(context);
		text.setLayoutParams(imgLayout);
		text.setTextSize(40);
		changeText();
		addView(text);

		// Button close
		Button closeButton = new Button(context);
		RelativeLayout.LayoutParams buttonLayout = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		buttonLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		buttonLayout.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		closeButton.setLayoutParams(buttonLayout);
		closeButton.setText("Close");
		closeButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent arg1) {
				closeMe();
				return false;
			}
		});
		addView(closeButton);

		// Button panel
		if (withButtonPanel){
			Button play = new Button(context);
			RelativeLayout.LayoutParams bl = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			bl.addRule(RelativeLayout.CENTER_IN_PARENT);
			play.setLayoutParams(bl);
			play.setText("Play");
			play.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
					GameScreenActivity screen = (GameScreenActivity)sefl_.getContext();
					if (allCards_.get(current_).isPlayable()){
						if (allCards_.get(current_).getTrades().size() == 0){
							// Play card without trade
							screen.play(allCards_.get(current_).getName(), new HashMap<Resource, List<NeighborReference>>());
						} 
						else{ // Play card with trade
							if (event.getActionMasked() == MotionEvent.ACTION_DOWN){
								// Show trade popup - Fixme: send all trades to popup
								showTradePopup(allCards_.get(current_).getTrades());
							}
						}
					}
					closeMe();
					return false;
				}
			});

			Button discard = new Button(context);
			discard.setLayoutParams(bl);
			discard.setText("Discard");

			Button wonders = new Button(context);
			wonders.setLayoutParams(bl);
			wonders.setText("Wonders");
			if (canPlayWonder)
				wonders.setOnTouchListener(new OnTouchListener() {				
					@Override
					public boolean onTouch(View arg0, MotionEvent event) {
						int action = event.getActionMasked();
						if (action == MotionEvent.ACTION_DOWN){
							// Fixme: Show the trade popup for wonders
							//showTradePopup();
							// Fixme: PlayWonder
							//playWonder();
							closeMe();
						}
						return false;
					}
				});
			else 
				wonders.setEnabled(false);

			LinearLayout ln = new LinearLayout(context);
			ln.setOrientation(LinearLayout.VERTICAL);
			ln.setGravity(Gravity.CENTER_VERTICAL);
			ln.setBackgroundColor(Color.RED);
			ln.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
			ln.addView(play);
			ln.addView(wonders);
			ln.addView(discard);
			addView(ln);
		}

		flingGesture_ = new OnFlingGestureListener(){
			@Override
			public void onRightToLeft() {
				// TODO Auto-generated method stub
				right();
				sefl_.invalidate();
			}
			@Override
			public void onLeftToRight() {
				// TODO Auto-generated method stub
				left();
				sefl_.invalidate();
			}
			@Override
			public void onBottomToTop() {
				// TODO Auto-generated method stub
			}
			@Override
			public void onTopToBottom() {
				// TODO Auto-generated method stub
			}
		};
		sefl_.setOnTouchListener(flingGesture_);
	}

	public ZoomCardView(Context context, List<CardInfo> cards, int current,
			boolean withButtonPanel, boolean canPlayWonder){
		super(context);
		init(context, cards, current, withButtonPanel,canPlayWonder);
	}

	public ZoomCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//init(context);
	}

	private void left(){
		if (current_ > 0){
			current_--;
			ImageView img = (ImageView)findViewWithTag("imageView");
			img.setImageBitmap(CardLoader.getInstance()
					.getBitmap(sefl_.getContext(), allCards_.get(current_).getName()));
			changeText();
		}
	}

	private void right(){
		if (current_+1 < allCards_.size()){
			current_++;
			ImageView img = (ImageView)findViewWithTag("imageView");
			img.setImageBitmap(CardLoader.getInstance()
					.getBitmap(sefl_.getContext(), allCards_.get(current_).getName()));
			changeText();
		}
	}

	private void closeMe(){
		sefl_.setVisibility(INVISIBLE);
	}

	private void showTradePopup(Set<Map<Resource,List<NeighborReference>>> trades){
		GameScreenActivity screen = (GameScreenActivity)sefl_.getContext();
		FragmentManager manager = screen.getFragmentManager();
		TradePopup tv = TradePopup.newInstance(allCards_.get(current_).getName(), trades); 
		tv.show(manager, "Trades");
	}
}
