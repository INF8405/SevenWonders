package ca.polymtl.inf8405.sevenwonders;

import ca.polymtl.inf8405.sevenwonders.api.Card;
import ca.polymtl.inf8405.sevenwonders.api.CardCategory;
import ca.polymtl.inf8405.sevenwonders.api.Player;
import static ca.polymtl.inf8405.sevenwonders.api.CardCategory.*;
import ca.polymtl.inf8405.sevenwonders.model.*;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameScreenFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		playerPosition = args.getInt("position");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView_ = (ViewGroup) inflater.inflate( R.layout.activity_game_screen, 
				container, false);
		Map<CardCategory,CardView> categoryToView_ = new HashMap<CardCategory, CardView>();

		LinearLayout playersInfoView = (LinearLayout)rootView_.findViewById(R.id.PlayerInfos);
		playersInfoView.setBackgroundColor(Color.RED);
		
		// Set handView height
		LinearLayout boardView = (LinearLayout)rootView_.findViewById(R.id.TopBoardView);
		LayoutParams params = boardView.getLayoutParams();
		params.height = (int)(GameScreenActivity.SCREEN_HEIGTH * BOARD_VIEW_WEIGHT / (STATE_VIEW_WEIGHT + BOARD_VIEW_WEIGHT));

		// Cards in hand
		PlayerStateView handView = (PlayerStateView)rootView_.findViewById(R.id.PlayerStateView);
		handView.setCardSize(GameScreenActivity.SCREEN_HEIGTH * STATE_VIEW_WEIGHT / (STATE_VIEW_WEIGHT + BOARD_VIEW_WEIGHT));
		List<CardInfo> cards = new LinkedList<CardInfo>(); 
		for (Card c: ScreenSlidePagerAdapter.hand_.playables.keySet()){
			cards.add(new CardInfo(c, true, ScreenSlidePagerAdapter.hand_.playables.get(c)));
		}
		for (Card c: ScreenSlidePagerAdapter.hand_.unplayables){
			cards.add(new CardInfo(c));
		}
		handView.setCards(cards);
		handView.setPlayer(ScreenSlidePagerAdapter.players_.get(playerPosition));

		if (isOpponent()){ handView.setAlpha((float)0.5); }

		// Update board view
		Map<CardCategory, List<Card>> cardsOnBoard = ScreenSlidePagerAdapter.players_.get(playerPosition).getTableau();
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
		
		// Update player info
		Player currentPlayer = ScreenSlidePagerAdapter.players_.get(playerPosition);
		TextView name = (TextView) rootView_.findViewById(R.id.PlayerName);
		name.setText(currentPlayer.username);
		
		TextView wonders = (TextView) rootView_.findViewById(R.id.WondersStaged);
		wonders.setText("Wonders staged:" + currentPlayer.getWonderStaged());
		
		TextView coins = (TextView) rootView_.findViewById(R.id.CoinsTextView);
		coins.setText("" + currentPlayer.coins);
		
		int[] battleMarkerPoint = {1,3,5,-1};
		Map<Integer, Integer> battleMarkers = new HashMap<Integer, Integer>(4);
		for (int i: battleMarkerPoint){
			int occurrences = Collections.frequency(currentPlayer.getBattleMarkers(), i);
			battleMarkers.put(i, occurrences);
		}
		 
		((TextView)rootView_.findViewById(R.id.BMAge1)).setText(battleMarkers.get(1)+"");
		((TextView)rootView_.findViewById(R.id.BMAge2)).setText(battleMarkers.get(3)+"");
		((TextView)rootView_.findViewById(R.id.BMAge3)).setText(battleMarkers.get(5)+"");
		((TextView)rootView_.findViewById(R.id.BD)).setText(battleMarkers.get(-1)+"");
		
		//		// Fixme: Implement me duc !
		//		//player.canPlayWonder - in PlayerStateView - DONE
		//		//player.civilisation - in PlayerStateView & ScoreBoardView - DONE
		//		//player.wonderStaged - in PlayerStateView & ScoreBoardView - DONE
		//		//player.coins - in ScoreBoardView ( & PlayerStateView ) - DONE
		//		//player.battleMarkers - in ScoreBoardView - DONE
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

	private static final int BOARD_VIEW_WEIGHT = 1;
	private static final int STATE_VIEW_WEIGHT = 1;

	public int playerPosition;

	private boolean isOpponent() {
		return playerPosition != (ScreenSlidePagerAdapter.players_.size()-1);
	}

}
