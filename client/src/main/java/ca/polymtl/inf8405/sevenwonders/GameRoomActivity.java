package ca.polymtl.inf8405.sevenwonders;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class GameRoomActivity extends Activity{
	private static ArrayAdapter adapter_;
    private static ArrayList<String> players_ = new ArrayList<String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_game_room);

		// Create a list view of game room
		final ListView listView = (ListView) findViewById(R.id.listView);
		adapter_ = new ArrayAdapter<String>(this, R.layout.gameroom_item,players_);
		listView.setAdapter(adapter_);
		listView.setTextFilterEnabled(true);
	}
	
	public void play(View view){
		// TODO: Update player list
		Toast.makeText(getApplicationContext(), "Joined", Toast.LENGTH_SHORT).show();
	}
}
