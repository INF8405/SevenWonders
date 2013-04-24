package ca.polymtl.inf8405.sevenwonders;

import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import ca.polymtl.inf8405.sevenwonders.api.GameState;
import ca.polymtl.inf8405.sevenwonders.api.Hand;
import ca.polymtl.inf8405.sevenwonders.api.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A simple pager adapter that represents all player objects, in
 * sequence.
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

	public ScreenSlidePagerAdapter(FragmentManager fm, List<Player> players) {
		super(fm);
		players_ = players;
	}

	@Override
	public Fragment getItem(int position) {
		String s = position + " - ";
		position = position % players_.size();
		s += position + " - " + players_.get(position).getCivilisation().toString();
		Fragment fragment = new GameScreenFragment();
		Bundle args = new Bundle();
		args.putInt("position", position);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	public void setState( final GameState state ) {
		players_ = state.players;
		Collections.reverse(players_);
		hand_ = state.hand;
	}

	public static List<Player> players_;
	public static Hand hand_;
}