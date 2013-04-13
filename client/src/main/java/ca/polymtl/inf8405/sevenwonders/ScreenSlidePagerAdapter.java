package ca.polymtl.inf8405.sevenwonders;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import ca.polymtl.inf8405.sevenwonders.api.GameState;
import ca.polymtl.inf8405.sevenwonders.api.Player;

import java.util.List;

/**
 * A simple pager adapter that represents all player objects, in
 * sequence.
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

	public ScreenSlidePagerAdapter(FragmentManager fm, int count) {
        super(fm);
        count_ = count;
        for ( int position = 0; position < count_; position++ ){
            GameScreenFragment fragment = new GameScreenFragment(position);
            fragments.append(position,fragment);
        }
    }

	@Override
	public Fragment getItem(int position) {
		return fragments.get( position, null );
	}

	@Override
	public int getCount() {
		return count_;
	}

    public void initState(final GameState state) {
        List<Player> players = state.getPlayers();
        for( int i = 0; i < fragments.size(); i++ ){
            fragments.get(i).init(players.get(i), state.getHand());
        }
    }

    public void setState(final GameState state) {
        List<Player> players = state.getPlayers();
        for( int i = 0; i < fragments.size(); i++ ){
            fragments.get(i).update(players.get(i), state.getHand());
        }
        notifyDataSetChanged();
    }

    private int count_;
    private SparseArray<GameScreenFragment> fragments = new SparseArray<GameScreenFragment>();
}