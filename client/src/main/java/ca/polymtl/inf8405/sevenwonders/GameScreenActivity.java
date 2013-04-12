package ca.polymtl.inf8405.sevenwonders;

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
import ca.polymtl.inf8405.sevenwonders.api.NeighborReference;
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
		mPager = (ViewPager) findViewById(R.id.Pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), 3);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(0);
        // End Testing code - Comment it when testing with server

		// Get ScreenSize
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		SCREEN_WIDTH = size.x;
		SCREEN_HEIGTH = size.y;
	}

	public static void showZoomPopup(View view, int selectedCardId, List<String> cardNames, boolean withButtonPanel) {
		PopupWindow popup = new PopupWindow();
		popup.setContentView(new ZoomCardView(view.getContext(), cardNames, selectedCardId, withButtonPanel));
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
