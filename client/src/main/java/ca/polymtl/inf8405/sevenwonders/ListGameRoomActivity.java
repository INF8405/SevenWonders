package ca.polymtl.inf8405.sevenwonders;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ListGameRoomActivity extends Activity implements LocationListener{

	private static ArrayAdapter adapter_;
	static ArrayList<String> ROOMS = new ArrayList<String>();
	private LocationManager locationManager_;
	/// Temporary String
	private String locationString_ = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_game_rooms);

		// Create a list view of game room
		final ListView listView = (ListView) findViewById(R.id.listView);
		adapter_ = new ArrayAdapter<String>(this, R.layout.gameroom_item,ROOMS);
		listView.setAdapter(adapter_);
		listView.setTextFilterEnabled(true);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO: Join game room
				Toast.makeText(getApplicationContext(),
						((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			}
		});

		// Create location manager 
		locationManager_ = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the locatioin provider -> use
		// default
		Criteria criteria = new Criteria();
		String provider = locationManager_.getBestProvider(criteria, false);
		Location location = locationManager_.getLastKnownLocation(provider);
		if (location != null) {
			onLocationChanged(location);
		} else {
			locationString_ = "Location not available";
		}
	}

	public void createGame(View view){
		// TODO: Send request Create (locationString) to server and wait
		int random = 1000 + (int)(Math.random() * ((9999 - 1000) + 1));
		ROOMS.add("Room " + random + " - " + locationString_);

		adapter_.notifyDataSetChanged();
	}

	//////////////////////////// Location Listener ///////////////////////////////////// 
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		locationString_ = "Current location = " +
				String.valueOf(location.getLatitude()) + " ; " +
				String.valueOf(location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}
}
