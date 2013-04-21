package ca.polymtl.inf8405.sevenwonders;

import ca.polymtl.inf8405.sevenwonders.api.Card;
import ca.polymtl.inf8405.sevenwonders.api.CardCategory;
import static ca.polymtl.inf8405.sevenwonders.api.CardCategory.*;
import ca.polymtl.inf8405.sevenwonders.model.*;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameScreenFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		position = args.getInt("position");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView_ = (ViewGroup) inflater.inflate( R.layout.activity_game_screen, 
				container, false);
		Map<CardCategory,CardView> categoryToView_ = new HashMap<CardCategory, CardView>();

		// Set handView height
		LinearLayout boardView = (LinearLayout)rootView_.findViewById(R.id.TopBoardView);
		LayoutParams params = boardView.getLayoutParams();
		params.height = (int)(GameScreenActivity.SCREEN_HEIGTH * BOARD_VIEW_WEIGHT / (STATE_VIEW_WEIGHT + BOARD_VIEW_WEIGHT));

		// Cards in hand
		PlayerStateView handView = (PlayerStateView)rootView_.findViewById(R.id.PlayerStateView);
		handView.setCardSize(GameScreenActivity.SCREEN_HEIGTH * STATE_VIEW_WEIGHT / (STATE_VIEW_WEIGHT + BOARD_VIEW_WEIGHT));
		List<CardInfo> cards = new LinkedList<CardInfo>(); // Fixme: unplayables vs playables
		for (Card c: ScreenSlidePagerAdapter.hand_.playables.keySet()){
			cards.add(new CardInfo(c, true, ScreenSlidePagerAdapter.hand_.playables.get(c)));
		}
		for (Card c: ScreenSlidePagerAdapter.hand_.unplayables){
			cards.add(new CardInfo(c));
		}
		handView.setCards(cards);
		handView.setPlayer(ScreenSlidePagerAdapter.players_.get(position));

		if (isOpponent()){ handView.setAlpha((float)0.5); }

		Map<CardCategory, List<Card>> cardsOnBoard = ScreenSlidePagerAdapter.players_.get(position).getTableau();

		CardView c1 = (CardView) rootView_.findViewById(R.id.BlueCard);
		categoryToView_.put( CIVILIAN,  c1);
		CardView c2 = (CardView) rootView_.findViewById(R.id.YellowCard);
		categoryToView_.put( COMMERCIAL, c2);
		categoryToView_.put( GUILD, (CardView) rootView_.findViewById(R.id.GillCard));
		categoryToView_.put( MANUFACTURED_GOOD, (CardView) rootView_.findViewById(R.id.AdvancedResourceView));
		categoryToView_.put( MILITARY, (CardView) rootView_.findViewById(R.id.RedCard));
		categoryToView_.put( RAW_MATERIAL, (CardView) rootView_.findViewById(R.id.BasicResourceView));
		categoryToView_.put( SCIENCE, (CardView) rootView_.findViewById(R.id.GreenCard));

		for(Map.Entry<CardCategory,List<Card>> entry : cardsOnBoard.entrySet()){
			categoryToView_.get(entry.getKey()).setCards(toCardInfoList(entry.getValue()));
		}
		
		//		// Fixme: Implement me duc !
		//		//player.canPlayWonder - in PlayerStateView - DONE
		//		//player.civilisation - in PlayerStateView & ScoreBoardView - DONE
		//		//player.wonderStaged - in PlayerStateView & ScoreBoardView
		//		//player.coins - in ScoreBoardView ( & PlayerStateView )
		//		//player.battleMarkers - in ScoreBoardView
		//		//player.score - in ScoreBoardView

		return rootView_;
	}

	private List<CardInfo> toCardInfoList(List<Card> cards){
		List<CardInfo> result = new ArrayList<CardInfo>();
		if (cards.size() > 0){
			for (Card card: cards){
				result.add(new CardInfo(card));
			}
		}
		return result;
	}

	private static final int BOARD_VIEW_WEIGHT = 3;
	private static final int STATE_VIEW_WEIGHT = 2;

	private int position;

	private boolean isOpponent() {
		return position != 0; // Fixme => Why??? U're Good!!
	}

}
