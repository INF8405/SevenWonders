package ca.polymtl.inf8405.sevenwonders;


//import PlayerStateView;

import java.util.List;

import ca.polymtl.inf8405.sevenwonders.R;
import ca.polymtl.inf8405.sevenwonders.controller.PlayerManager;
import android.support.v4.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.PopupWindow;


public class ScreenSlidePagerActivity extends FragmentActivity {
	public static int SCREEN_HEIGTH ;
	public static int SCREEN_WIDTH;
	
	/**
	 * The pager widget, which handles animation and allows swiping horizontally to access previous
	 * and next wizard steps.
	 */
	private ViewPager mPager;

	/**
	 * The pager adapter, which provides the pages to the view pager widget.
	 */
	private PagerAdapter mPagerAdapter;

	private static PlayerManager manager_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_screen_slide);

		if (manager_ == null)
			manager_ = new PlayerManager();

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.Pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setCurrentItem(manager_.getMyId());
		//mPager.requestDisallowInterceptTouchEvent(true);
		
		// Get ScreenSize
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		SCREEN_WIDTH = size.x;
		SCREEN_HEIGTH = size.y;
	}

//	@Override
//	public void onBackPressed() {
//		if (mPager.getCurrentItem() == 0) {
//			// If the user is currently looking at the first step, allow the system to handle the
//			// Back button. This calls finish() on this activity and pops the back stack.
//			super.onBackPressed();
//		} else {
//			// Otherwise, select the previous step.
//			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
//		}
//	}
	
	/**
	 * A simple pager adapter that represents all player objects, in
	 * sequence.
	 */
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			boolean isMe = manager_.getPlayer(position).equals(manager_.getMe());
			GameScreenFragment newFragment = new GameScreenFragment(manager_.getPlayer(position), 
					manager_.getHand(), isMe); 
			registeredFragments.put(position, newFragment);
			return newFragment;
		}

		@Override
		public int getCount() {
			return manager_.getPlayers().size();
		}
		
		public Fragment getRegisteredFragment(int position){
			return registeredFragments.get(position);
		}
		
		public void updateFragments(){
			for (int i = 0 ; i < registeredFragments.size(); i++){
				GameScreenFragment fragment = (GameScreenFragment)registeredFragments.get(i);
				fragment.updateBoard(manager_.getPlayer(i));
			}
		}
	}
	
	public static void showZoomPopup(View view, int selectedCardId, List<String> cardNames, 
			boolean withButtonPanel){
		PopupWindow popup = new PopupWindow();
		popup.setContentView(new ZoomCardView(view.getContext(), cardNames, selectedCardId, withButtonPanel));
		popup.showAtLocation(view, Gravity.CENTER, 0, 0);
		popup.update(0, 0, SCREEN_WIDTH*2/3, SCREEN_HEIGTH/2);
	}

	public void play(String cardName){
		// Update modele
		manager_.play(cardName);

		// Update other views
		PlayerStateView handView = (PlayerStateView)findViewById(R.id.PlayerStateView);
		handView.play(cardName);
		ScreenSlidePagerAdapter fragmentPagerAdapter = (ScreenSlidePagerAdapter) mPager.getAdapter();
		fragmentPagerAdapter.updateFragments();
		//updateBoard(manager_.getMe());
		
	}

	
}
