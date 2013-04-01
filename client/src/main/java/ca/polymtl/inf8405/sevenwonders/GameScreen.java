package ca.polymtl.inf8405.sevenwonders;

import ca.polymtl.inf8405.sevenwonders.R;
import ca.polymtl.inf8405.sevenwonders.controller.*;
import ca.polymtl.inf8405.sevenwonders.model.*;

import android.app.Activity;
import android.app.FragmentManager;
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

//	private OnFlingGestureListener flingGesture_;
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
		handView.setCards(manager_.getHand());

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

		/*		for (int i=0; i<2; i++){
			basicResources.addCard(cards.get(i));
			advancedResources.addCard(cards.get(i));
		}
		 */

		updateBoard(currentPlayer);
	}	

//	public boolean dispatchTouchEvent(MotionEvent ev){
//		super.dispatchTouchEvent(ev);
//		View topView = findViewById(R.id.TopBoardView);
//		return flingGesture_.onTouch(topView, ev);
//	}

	public void play(String cardName){
		// Update modele
		manager_.play(cardName);

		// Update other views
		PlayerStateView handView = (PlayerStateView)findViewById(R.id.PlayerStateView);
		handView.play(cardName);
		updateBoard(manager_.getMe());
	}

	private void updateBoard(Player currentPlayer){
		PlayedCards green = (PlayedCards) findViewById(R.id.GreenCard);
		PlayedCards red = (PlayedCards) findViewById(R.id.RedCard);
		PlayedCards blue = (PlayedCards) findViewById(R.id.BlueCard);
		PlayedCards yellow = (PlayedCards) findViewById(R.id.YellowCard);
		PlayedCards gill = (PlayedCards) findViewById(R.id.GillCard);
		ResourceView basicResources = (ResourceView)findViewById(R.id.BasicResourceView);
		ResourceView advancedResources = (ResourceView)findViewById(R.id.AdvancedResourceView);

		green.setCards(currentPlayer.getPlayedCards("science"));
		red.setCards(currentPlayer.getPlayedCards("military"));
		blue.setCards(currentPlayer.getPlayedCards("culture"));
		yellow.setCards(currentPlayer.getPlayedCards("trade"));
		gill.setCards(currentPlayer.getPlayedCards("gill"));
		basicResources.setCards(currentPlayer.getPlayedCards("basicRessource"));
		advancedResources.setCards(currentPlayer.getPlayedCards("advancedRessource"));
	}
}
