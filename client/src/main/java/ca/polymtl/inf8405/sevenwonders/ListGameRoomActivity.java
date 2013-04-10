package ca.polymtl.inf8405.sevenwonders;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
 
public class ListGameRoomActivity extends Activity {
 
	private ArrayAdapter adapter;
	static String[] FRUITS = new String[] { "Apple", "Avocado", "Banana",
			"Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
			"Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple",
			"Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple"};
 
	static ArrayList<String> ROOMS = new ArrayList<String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_game_rooms);
 
		final ListView listView = (ListView) findViewById(R.id.listView);
		adapter = new ArrayAdapter<String>(this, R.layout.gameroom_item,ROOMS);
		listView.setAdapter(adapter);
 
		listView.setTextFilterEnabled(true);
 
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			    // When clicked, show a toast with the TextView text
			    Toast.makeText(getApplicationContext(),
				((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public void createGame(View view){
		int random = 1000 + (int)(Math.random() * ((9999 - 1000) + 1));
		ROOMS.add("Room " + random);
		adapter.notifyDataSetChanged();
	}
}
