package ca.polymtl.inf8405.sevenwonders;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import ca.polymtl.inf8405.sevenwonders.controller.PlayerManager;

/**
 * A simple pager adapter that represents all player objects, in
 * sequence.
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
	private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    private PlayerManager manager_;
	public ScreenSlidePagerAdapter(FragmentManager fm,PlayerManager manager) {
		super(fm);
        manager_ = manager;
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