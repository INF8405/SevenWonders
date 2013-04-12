package ca.polymtl.inf8405.sevenwonders;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ca.polymtl.inf8405.sevenwonders.api.CardCategory;

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
		// TESTING : Test UI without Server
//		int random = 0 + (int)(Math.random() * ((11 - 0) + 1));
//		handView.setCivilisation(random+"");

		if (isOpponent()){ handView.setAlpha((float)0.5); }

		if (categoryToView_.size() == 0){
			categoryToView_.put( CardCategory.Civilian, (CardView) rootView_.findViewById(R.id.BlueCard) );
			categoryToView_.put( CardCategory.Commercial, (CardView) rootView_.findViewById(R.id.YellowCard));
			categoryToView_.put( CardCategory.Guild, (CardView) rootView_.findViewById(R.id.GillCard));
			categoryToView_.put( CardCategory.ManufacturedGoods, (CardView) rootView_.findViewById(R.id.AdvancedResourceView));
			categoryToView_.put( CardCategory.Military, (CardView) rootView_.findViewById(R.id.RedCard));
			categoryToView_.put( CardCategory.RawMaterial, (CardView) rootView_.findViewById(R.id.BasicResourceView));
			categoryToView_.put( CardCategory.Science, (CardView) rootView_.findViewById(R.id.GreenCard));
		}
		
		return rootView_;
	}

	public void update(Player player, Hand hand ){

		PlayerStateView handView = (PlayerStateView)rootView_.findViewById(R.id.PlayerStateView);

		List<String> cards = new LinkedList<String>(); // Fixme: unplayables vs playables
		cards.addAll(hand.getPlayables().keySet());
		cards.addAll(hand.getUnplayables());
		handView.setCards(cards);
		handView.setCivilisation(player.civilisation);
		
		// Fixme: Implement me duc !
		//player.civilisation
		//player.canPlayWonder
		//player.battleMarkers
		//player.coins
		//player.score
		//player.wonderStaged

		for( Map.Entry<CardCategory,List<String>> entry : player.getTableau().entrySet() ) {
			categoryToView_.get(entry.getKey()).setCards( entry.getValue());
		}
	}

	private static final int BOARD_VIEW_WEIGHT = 3;
	private static final int STATE_VIEW_WEIGHT = 2;

	private int position;

	private boolean isOpponent() {
		return position != 0; // Fixme
	}

	private ViewGroup rootView_;
	private static Map<CardCategory,CardView> categoryToView_ = new HashMap<CardCategory, CardView>();
}
