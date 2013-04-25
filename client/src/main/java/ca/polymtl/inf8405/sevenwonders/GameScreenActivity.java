package ca.polymtl.inf8405.sevenwonders;

import ca.polymtl.inf8405.sevenwonders.model.*;

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

import ca.polymtl.inf8405.sevenwonders.api.*;

import org.apache.thrift.TException;


public class GameScreenActivity extends FragmentActivity {

	public static int SCREEN_HEIGTH;
	public static int SCREEN_WIDTH;

	public GameScreenActivity() {
		if (MainActivity.DEBUG_MODE){
			ReceiverStub.getInstance().addObserver( new ApiDelegate() );
		}
		else {
			Receiver.getInstance().addObserver( new ApiDelegate() );
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_screen_slide);

		// Get ScreenSize
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		SCREEN_WIDTH = size.x;
		SCREEN_HEIGTH = size.y;

		if (MainActivity.DEBUG_MODE){
			ReceiverStub.getInstance().simulate_c_begin(); // Fixme: test

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(5000);
						ReceiverStub.getInstance().simulate_c_sendState();
					} catch ( InterruptedException e ) {
					}
				}
			}).start();
		}
		else {
			GameState state = (GameState) getIntent().getSerializableExtra(GameRoomActivity.MESSAGE_GAME_BEGIN);
			mPager = (ViewPager) findViewById(R.id.Pager);
			mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), state.getPlayers());
			mPager.setAdapter(mPagerAdapter);
			setState(state);
		}
	}

	public static void showZoomPopup(View view, int selectedCardId, List<CardInfo> cards, boolean withButtonPanel, boolean canPlayWonder) {
		PopupWindow popup = new PopupWindow();
		popup.setContentView(new ZoomCardView(view.getContext(), cards, selectedCardId, withButtonPanel, canPlayWonder));
		popup.showAtLocation(view, Gravity.CENTER, 0, 0);
		popup.update(0, 0, SCREEN_WIDTH*2/3, SCREEN_HEIGTH/2);
	}

	public static void play(Card card, Map<Resource, List<NeighborReference>> trade, boolean wonder ) {
		if (MainActivity.DEBUG_MODE){
			Log.e("GameScreenActivity", "Play card: " + card.toString() + " - wonders:" + wonder);
		} else {
			try {
				if( wonder ) {
					Sender.getInstance().s_playWonder( card, trade );
				} else {
					Sender.getInstance().s_playCard( card, trade );
				}
			} catch ( TException e ){
				Log.e("Game", e.getMessage() );
			}	
		}
	}

	public static void discard(Card card) {
		try {
			Sender.getInstance().s_discard( card );
		} catch ( TException e ){
			Log.e("Game", e.getMessage() );
		}
	}

	private class ApiDelegate extends Api {
		@Override public void c_sendState(final GameState state) throws TException {
			runOnUiThread(new Runnable() {
				@Override public void run() {
					setState(state);
				}
			});
		}
		@Override public void c_sendEndState(final GameState state, List<Map<String, Integer>> detail) throws TException {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					setState(state);
				}
			});
		}

		@Override public void c_begin(final GameState state) throws TException {
            if (MainActivity.DEBUG_MODE){
                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        mPager = (ViewPager) findViewById(R.id.Pager);
                        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), state.getPlayers());
                        mPager.setAdapter(mPagerAdapter);
                        setState(state);
                    }
                });
            }
		}
	}
	
	private void setState( final GameState state ) {
		mPager.setCurrentItem(state.getPlayersSize() -1 + (50*state.getPlayersSize()));//Fixme : Magic number 50
		mPagerAdapter.setState(state);
		mPagerAdapter.notifyDataSetChanged();
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
