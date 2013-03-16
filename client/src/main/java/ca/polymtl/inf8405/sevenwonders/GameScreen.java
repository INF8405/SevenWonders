package ca.polymtl.inf8405.sevenwonders;

import ca.polymtl.inf8405.sevenwonders.R;
import ca.polymtl.inf8405.sevenwonders.controller.*;
import ca.polymtl.inf8405.sevenwonders.model.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.*;

public class GameScreen extends Activity {
	public static int SCREEN_HEIGTH ;
	public static int SCREEN_WIDTH;
	
	private static final int BOARD_VIEW_WEIGHT = 3;
	private static final int STATE_VIEW_WEIGHT = 2;
	
	private static String PLAYER_ID_MESSAGE = "playerId";
	private static PlayerManager manager_;
	
	private OnFlingGestureListener flingGesture_;
	private int playerId_;
	private Activity seft_ = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_game_screen);

		if (manager_ == null)
			manager_ = new PlayerManager();

		// Get ScreenSize
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		SCREEN_WIDTH = size.x;
		SCREEN_HEIGTH = size.y;

		// Set boardView height
		LinearLayout boardView = (LinearLayout)findViewById(R.id.TopBoardView);
		LayoutParams params = boardView.getLayoutParams();
		params.height = (int)(size.y * BOARD_VIEW_WEIGHT / (STATE_VIEW_WEIGHT + BOARD_VIEW_WEIGHT));

		PlayerStateView handView = (PlayerStateView)findViewById(R.id.PlayerStateView);
		handView.setCardSize(size.y * STATE_VIEW_WEIGHT 
				/ (STATE_VIEW_WEIGHT + BOARD_VIEW_WEIGHT));
		// set hand cards for me
		handView.setCards(manager_.getMe().getHand());

		// Receive parameters
		playerId_ = getIntent().getIntExtra(PLAYER_ID_MESSAGE, -1);
		Player currentPlayer;

		if ((playerId_ == -1) || (playerId_ == manager_.getMyId())){
			playerId_ = manager_.getCurrentId();
			currentPlayer = manager_.getMe();
		}
		else{
			currentPlayer = manager_.getPlayer(playerId_);
			handView.setAlpha((float)0.5);
		}

		ResourceView basicResources = (ResourceView)findViewById(R.id.BasicResourceView);
		ResourceView advancedResources = (ResourceView)findViewById(R.id.AdvancedResourceView);
		/*		for (int i=0; i<2; i++){
			basicResources.addCard(cards.get(i));
			advancedResources.addCard(cards.get(i));
		}
		 */

		PlayedCards green = (PlayedCards) findViewById(R.id.GreenCard);
		PlayedCards red = (PlayedCards) findViewById(R.id.RedCard);
		PlayedCards blue = (PlayedCards) findViewById(R.id.BlueCard);
		PlayedCards yellow = (PlayedCards) findViewById(R.id.YellowCard);
		PlayedCards gill = (PlayedCards) findViewById(R.id.GillCard);
		for (Card c: currentPlayer.getPlayed()){
			if (c.getClass() == MilitaryCard.class){
				red.addCard(c);
			}
			else 
				green.addCard(c);
		}

		// Set onFling Listener - TEMP - TO REFACTOR
		flingGesture_ = new OnFlingGestureListener(){
			@Override
			public void onRightToLeft() {
				// TODO Auto-generated method stub
				if (playerId_+1 < manager_.getPlayers().size()){
					Intent intent = new Intent(seft_, GameScreen.class);
					intent.putExtra(PLAYER_ID_MESSAGE, playerId_+1);
					startActivity(intent);
					seft_.finish();
				}
			}

			@Override
			public void onLeftToRight() {
				// TODO Auto-generated method stub
				if (playerId_ > 0){
					Intent intent = new Intent(seft_, GameScreen.class);
					intent.putExtra(PLAYER_ID_MESSAGE, playerId_-1);
					startActivity(intent);
					seft_.finish();
				}
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
		boardView.setOnTouchListener(flingGesture_);
	}	

	public boolean dispatchTouchEvent(MotionEvent ev){
		super.dispatchTouchEvent(ev);
		View topView = findViewById(R.id.TopBoardView);
		return flingGesture_.onTouch(topView, ev);
	}
	
	public static void showZoomPopup(View view, List<String> cardNames, Context context){
		PopupWindow popup = new PopupWindow(view);
		popup.setContentView(new ZoomCardView(context, cardNames, 0)); 
		popup.showAtLocation(view, Gravity.CENTER, 0, 0);
		popup.update(0, 0, GameScreen.SCREEN_WIDTH*2/3, GameScreen.SCREEN_HEIGTH/2);
	}

}
