package ca.polymtl.inf8405.sevenwonders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.PopupWindow;

import ca.polymtl.inf8405.sevenwonders.api.GameState;
import ca.polymtl.inf8405.sevenwonders.api.Hand;
import ca.polymtl.inf8405.sevenwonders.api.NeighborReference;
import ca.polymtl.inf8405.sevenwonders.api.Player;
import ca.polymtl.inf8405.sevenwonders.api.Resource;
import org.apache.thrift.TException;


public class GameScreenActivity extends FragmentActivity {

	public static int SCREEN_HEIGTH;
	public static int SCREEN_WIDTH;

    public GameScreenActivity() {
        Receiver.getInstance().addObserver( new ApiDelegate() );
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_screen_slide);

		//mPager.requestDisallowInterceptTouchEvent(true);
		
		// TESTING: Test UI without server - Duc - I'll kill you if u try to remove theses lines Gui!
        int random = 0 + (int)(Math.random() * ((11 - 0) + 1));
		Player player = new Player();
		player.civilisation = random+"";
		player.canPlayWonder = true;
		List<Player> players = new ArrayList<Player>();
		players.add(player);
		for(int i = 0 ; i < 3; i++){
			player = new Player();
			player.civilisation = i+"";
			players.add(player);
		}
		
		List<String> cards = new ArrayList<String>(7);
		for (int i = 0 ; i < 7; i++){
			random = 0 + (int)(Math.random() * ((21 - 0) + 1));
			cards.add(random+"");
		}
		
		Hand hand = new Hand();
		hand.unplayables = cards;
        GameState state = new GameState();
        state.hand = hand;
        state.players = players;
        
        mPager = (ViewPager) findViewById(R.id.Pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), players.size());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(0);
        mPagerAdapter.setState(state);
        // End Testing code - Comment it when testing with server

		// Get ScreenSize
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		SCREEN_WIDTH = size.x;
		SCREEN_HEIGTH = size.y;
	}

	public static void showZoomPopup(View view, int selectedCardId, List<String> cardNames, 
			boolean withButtonPanel, boolean canPlayWonder) {
		PopupWindow popup = new PopupWindow();
		popup.setContentView(new ZoomCardView(view.getContext(), cardNames, selectedCardId, 
				withButtonPanel, canPlayWonder));
		popup.showAtLocation(view, Gravity.CENTER, 0, 0);
		popup.update(0, 0, SCREEN_WIDTH*2/3, SCREEN_HEIGTH/2);
	}

	public void play(String cardName) {
        try {
            Sender.getInstance().s_playCard( cardName, new HashMap<Resource, List<NeighborReference>>() ); // Fixme Trade
        } catch ( TException e ){
            Log.e("Game", e.getMessage() );
        }
	}

    private class ApiDelegate extends Api {
        @Override public void c_begin(final GameState state) throws TException {
            runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    mPager = (ViewPager) findViewById(R.id.Pager);
                    mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), state.getPlayersSize());
                    mPager.setAdapter(mPagerAdapter);
                    mPager.setCurrentItem(0);

                    setState(state);
                }
            });
        }
        @Override public void c_sendState(final GameState state) throws TException {
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    setState( state );
                }
            });
        }
        @Override public void c_sendEndState(final GameState state, List<Map<String, Integer>> detail) throws TException {
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    setState(state);
                }
            });
        }

        private void setState( final GameState state ) {
            mPagerAdapter.setState(state);
        }
    }

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private ScreenSlidePagerAdapter mPagerAdapter;
}
