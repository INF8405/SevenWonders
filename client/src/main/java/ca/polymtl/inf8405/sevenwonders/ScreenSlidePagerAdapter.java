package ca.polymtl.inf8405.sevenwonders;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import ca.polymtl.inf8405.sevenwonders.api.GameState;
import ca.polymtl.inf8405.sevenwonders.api.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A simple pager adapter that represents all player objects, in
 * sequence.
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

	public ScreenSlidePagerAdapter(FragmentManager fm, int count ) {
        super(fm);
        count_ = count;
    }

	@Override
	public Fragment getItem(int position) {
        GameScreenFragment fragment = fragments.get( position, null );
        if( fragment == null ){
            fragment = new GameScreenFragment(position);
            fragments.append(position,fragment);
        }
		return fragment;
	}

	@Override
	public int getCount() {
		return count_;
	}

    public void setState( final GameState state ) {
        List<Player> players = state.getPlayers();
        for( int i = 0; i < fragments.size(); i++ ){
            fragments.get(i).update(players.get(i), state.getHand());
        }
    }

    private int count_;
    private SparseArray<GameScreenFragment> fragments = new SparseArray<GameScreenFragment>();
}