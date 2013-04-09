package ca.polymtl.inf8405.sevenwonders;

import java.util.List;

import ca.polymtl.inf8405.sevenwonders.model.Player;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class GameScreenFragment  extends Fragment{
	
	private static final int BOARD_VIEW_WEIGHT = 3;
	private static final int STATE_VIEW_WEIGHT = 2;

	private Player player_;
	private List<String> hand_;
	private boolean isMe_ = false;

	private ViewGroup rootView_;

	public GameScreenFragment(Player player, List<String> hand, boolean isMe){
		player_ = player;
		hand_ = hand;
		isMe_ = isMe;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView_ = (ViewGroup) inflater.inflate(
				R.layout.activity_game_screen, container, false);

		updateBoard(player_);

		// Set handView height
		LinearLayout boardView = (LinearLayout)rootView_.findViewById(R.id.TopBoardView);
		LayoutParams params = boardView.getLayoutParams();
		params.height = (int)(ScreenSlidePagerActivity.SCREEN_HEIGTH * BOARD_VIEW_WEIGHT / (STATE_VIEW_WEIGHT + BOARD_VIEW_WEIGHT));

		PlayerStateView handView = (PlayerStateView)rootView_.findViewById(R.id.PlayerStateView);
		handView.setCardSize(ScreenSlidePagerActivity.SCREEN_HEIGTH * STATE_VIEW_WEIGHT 
				/ (STATE_VIEW_WEIGHT + BOARD_VIEW_WEIGHT));
		// set hand cards for me
		handView.setCards(hand_);
		if (!isMe_)
			handView.setAlpha((float)0.5);

//		LinearLayout layout = (LinearLayout)rootView_.findViewById(R.id.TopBoardView);
//		layout.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				List<String> cardNames = new ArrayList<String>();
//				cardNames.add("0");
//				cardNames.add("1");
//				cardNames.add("2");
//				GameScreen.showZoomPopup(v, 0, cardNames, true);	
//				return false;
//			}
//		});

		return rootView_;
	}

	public void updateBoard(Player currentPlayer){
		PlayedCards green = (PlayedCards) rootView_.findViewById(R.id.GreenCard);
		PlayedCards red = (PlayedCards) rootView_.findViewById(R.id.RedCard);
		PlayedCards blue = (PlayedCards) rootView_.findViewById(R.id.BlueCard);
		PlayedCards yellow = (PlayedCards) rootView_.findViewById(R.id.YellowCard);
		PlayedCards gill = (PlayedCards) rootView_.findViewById(R.id.GillCard);
		ResourceView basicResources = (ResourceView)rootView_.findViewById(R.id.BasicResourceView);
		ResourceView advancedResources = (ResourceView)rootView_.findViewById(R.id.AdvancedResourceView);

		green.setCards(currentPlayer.getPlayedCards("science"));
		red.setCards(currentPlayer.getPlayedCards("military"));
		blue.setCards(currentPlayer.getPlayedCards("culture"));
		yellow.setCards(currentPlayer.getPlayedCards("trade"));
		gill.setCards(currentPlayer.getPlayedCards("gill"));
		basicResources.setCards(currentPlayer.getPlayedCards("basicRessource"));
		advancedResources.setCards(currentPlayer.getPlayedCards("advancedRessource"));
	}
	
}
