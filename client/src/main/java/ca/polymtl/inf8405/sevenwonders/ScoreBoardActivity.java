package ca.polymtl.inf8405.sevenwonders;

import java.util.ArrayList;

import ca.polymtl.inf8405.sevenwonders.api.Player;
import ca.polymtl.inf8405.sevenwonders.model.PlayerInfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.*;

public class ScoreBoardActivity extends Activity {
	private static ScoreBoardAdapter adapter_;
	private static List<PlayerInfo> scores_ = new ArrayList<PlayerInfo>();

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_scoreboard);

		final ListView listView = (ListView) findViewById(R.id.listScore);
		
		// Create our own version of the list adapter
		scores_ = getData();
		String[] from = new String[] {
				PlayerInfo.KEY_NAME, PlayerInfo.KEY_CIVILISATION, 
				PlayerInfo.KEY_COINS, PlayerInfo.KEY_WONDER_STAGE };
		int[] to = new int[] {
				R.id.name, R.id.civi, R.id.coins, R.id.wonderStaged};
		adapter_ = new ScoreBoardAdapter(this, scores_, R.layout.scoreboard_item, from, to);
		listView.setAdapter(adapter_);
	}

	public void closeScoreBoard(View view){
		finish();
	}
	////////////////////// TEST FUNCTION ///////////////////////////
	private List<PlayerInfo> getData(){
		List<PlayerInfo> infos = new ArrayList<PlayerInfo>();
		Player p = new Player();
		p.civilisation = "0";
		p.coins = 10;
		p.wonderStaged = 2;
		infos.add(new PlayerInfo(p));
		
		Player p1 = new Player();
		p1.civilisation = "0";
		p1.coins = 5;
		p1.wonderStaged = 4;
		infos.add(new PlayerInfo(p1));
		
		Player p2 = new Player();
		p2.civilisation = "0";
		p2.coins = 2;
		p2.wonderStaged = 0;
		infos.add(new PlayerInfo(p2));
		
		for (int i = 0 ; i < 7; i++){
			infos.add(new PlayerInfo(p2));
		}
		
		return infos;
	}
}
