package ca.polymtl.inf8405.sevenwonders;

import ca.polymtl.inf8405.sevenwonders.controller.CardLoader;
import ca.polymtl.inf8405.sevenwonders.controller.OnFlingGestureListener;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import java.util.*;

public class ZoomCardView extends RelativeLayout{

	private View sefl_ = this;
	private OnFlingGestureListener flingGesture_;
	private List<String> allCardNames_;
	private int current_;

	// Test - TO REMOVE
	private TextView text;
	private void changeText(){
		int max = 100;
		int min = 0;
		int random = min + (int)(Math.random() * ((max - min) + 1));
		text.setText("ab= " + random);
	}

	/**
	 * Build the zoom view
	 * @param context
	 * @param cardNames: list of all cards will be displayed in the zoom view
	 * @param current: the id of the current card
	 * @param withButtonPanel: if we want to display the button panel inside of the zoom view
	 */
	private void init(Context context, List<String> cardNames, int current, boolean withButtonPanel){
		current_ = current;
		allCardNames_ = cardNames;

		setBackgroundColor(Color.DKGRAY);
		setGravity(Gravity.RIGHT);

		// Image
		ImageView img = new ImageView(context);
		LinearLayout.LayoutParams imgLayout = new LinearLayout.LayoutParams
				(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		imgLayout.gravity = Gravity.CENTER;
		img.setLayoutParams(imgLayout);
		img.setImageBitmap(CardLoader.getInstance()
				.getBitmap(context, allCardNames_.get(current_)));
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
			public boolean onTouch(View arg0, MotionEvent arg1) {
				sefl_.setVisibility(INVISIBLE);
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

			Button discard = new Button(context);
			discard.setLayoutParams(bl);
			discard.setText("Discard");

			Button wonders = new Button(context);
			wonders.setLayoutParams(bl);
			wonders.setText("Wonders");

			LinearLayout ln = new LinearLayout(context);
			ln.setOrientation(LinearLayout.VERTICAL);
			ln.setGravity(Gravity.CENTER_VERTICAL);
			//ln.setBackgroundColor(Color.RED);
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

	public ZoomCardView(Context context, List<String> cardNames, int current, 
			boolean withButtonPanel){
		super(context);
		init(context, cardNames, current, withButtonPanel);
	}

	public ZoomCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//init(context);
	}

	private void left(){
		if (current_ > 0){
			changeText();
			current_--;
			ImageView img = (ImageView)findViewWithTag("imageView");
			img.setImageBitmap(CardLoader.getInstance()
					.getBitmap(sefl_.getContext(), allCardNames_.get(current_)));
		}
	}

	private void right(){
		if (current_+1 < allCardNames_.size()){
			changeText();
			current_++;
			ImageView img = (ImageView)findViewWithTag("imageView");
			img.setImageBitmap(CardLoader.getInstance()
					.getBitmap(sefl_.getContext(), allCardNames_.get(current_)));
		}
	}
}
