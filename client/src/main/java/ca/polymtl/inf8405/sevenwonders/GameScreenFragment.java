package ca.polymtl.inf8405.sevenwonders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ca.polymtl.inf8405.sevenwonders.api.Card;
import ca.polymtl.inf8405.sevenwonders.api.CardCategory;
import static ca.polymtl.inf8405.sevenwonders.api.CardCategory.*;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import ca.polymtl.inf8405.sevenwonders.api.Hand;
import ca.polymtl.inf8405.sevenwonders.api.Player;

public class GameScreenFragment extends Fragment {

	public GameScreenFragment( int position ) {
		this.position = position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView_ = (ViewGroup) inflater.inflate( R.layout.activity_game_screen, container, false);

		// Set handView height
		LinearLayout boardView = (LinearLayout)rootView_.findViewById(R.id.TopBoardView);
		LayoutParams params = boardView.getLayoutParams();
		params.height = (int)(GameScreenActivity.SCREEN_HEIGTH * BOARD_VIEW_WEIGHT / (STATE_VIEW_WEIGHT + BOARD_VIEW_WEIGHT));

		PlayerStateView handView = (PlayerStateView)rootView_.findViewById(R.id.PlayerStateView);
		handView.setCardSize(GameScreenActivity.SCREEN_HEIGTH * STATE_VIEW_WEIGHT / (STATE_VIEW_WEIGHT + BOARD_VIEW_WEIGHT));
		// TESTING : Test UI without Server ////////////////////////////////
//		int random = 0 + (int)(Math.random() * ((11 - 0) + 1));
//		Player player = new Player();
//		player.civilisation = random+"";
//		player.canPlayWonder = true;
//		handView.setPlayer(player);
//		List<String> cards = new ArrayList<String>(7);
//		for (int i = 0 ; i < 7; i++){
//			random = 0 + (int)(Math.random() * ((21 - 0) + 1));
//			cards.add(random+"");
//		}
//		handView.setCards(cards);
		// END TESTING //////////////////////////////////////////////

		if (isOpponent()){ handView.setAlpha((float)0.5); }

		if (categoryToView_.size() == 0){
			categoryToView_.put( CIVILIAN, (CardView) rootView_.findViewById(R.id.BlueCard) );
			categoryToView_.put( COMMERCIAL, (CardView) rootView_.findViewById(R.id.YellowCard));
			categoryToView_.put( GUILD, (CardView) rootView_.findViewById(R.id.GillCard));
			categoryToView_.put( MANUFACTURED_GOOD, (CardView) rootView_.findViewById(R.id.AdvancedResourceView));
			categoryToView_.put( MILITARY, (CardView) rootView_.findViewById(R.id.RedCard));
			categoryToView_.put( RAW_MATERIAL, (CardView) rootView_.findViewById(R.id.BasicResourceView));
			categoryToView_.put( SCIENCE, (CardView) rootView_.findViewById(R.id.GreenCard));
		}
		
		return rootView_;
	}

	public void update(Player player, Hand hand ){

		PlayerStateView handView = (PlayerStateView)rootView_.findViewById(R.id.PlayerStateView);

		List<Card> cards = new LinkedList<Card>(); // Fixme: unplayables vs playables
		cards.addAll(hand.getPlayables().keySet());
		cards.addAll(hand.getUnplayables());
		handView.setCards(cards);
		handView.setPlayer(player);
		
		// Fixme: Implement me duc !
		//player.canPlayWonder - in PlayerStateView - DONE
		//player.civilisation - in PlayerStateView & ScoreBoardView - DONE
		//player.wonderStaged - in PlayerStateView & ScoreBoardView
		//player.coins - in ScoreBoardView ( & PlayerStateView )
		//player.battleMarkers - in ScoreBoardView
		//player.score - in ScoreBoardView
//
//        for( Map.Entry<CardCategory,List<Card>> entry : player_.getTableau().entrySet() ) {
//            categoryToView_.get(entry.getKey()).setCards( entry.getValue());
//        }
	}

	private static final int BOARD_VIEW_WEIGHT = 3;
	private static final int STATE_VIEW_WEIGHT = 2;

	private int position;

	private boolean isOpponent() {
		return position != 0; // Fixme
	}

	private static ViewGroup rootView_;
	private static Map<CardCategory,CardView> categoryToView_ = new HashMap<CardCategory, CardView>();
}
