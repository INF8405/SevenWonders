package ca.polymtl.inf8405.sevenwonders;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import ca.polymtl.inf8405.sevenwonders.api.GameRoom;
import ca.polymtl.inf8405.sevenwonders.api.GameRoomDef;
import ca.polymtl.inf8405.sevenwonders.api.GeoLocation;
import org.apache.thrift.TException;

public class ListGameRoomActivity extends Activity implements LocationListener{

    public static String GAMEID_MESSAGE = "ListGameRoomActivity_GameId";

    public ListGameRoomActivity() {
        Receiver.getInstance().addObserver( new ApiDelegate() );
    }

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_game_rooms);

		// Create a list view of game room
		final ListView listView = (ListView) findViewById(R.id.listView);
		adapter_ = new ArrayAdapter<GameRoomAdapter>(this, R.layout.gameroom_item,rooms_);
		listView.setAdapter(adapter_);
		listView.setTextFilterEnabled(true);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent intent = new Intent(self, GameRoomActivity.class);
            intent.putExtra(GAMEID_MESSAGE,rooms_.get(position).room.id);
            startActivity(intent);
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
			locationSet = false;
		}
	}

	public void createGame(View view) throws TException {

        Intent intent = new Intent(this, GameRoomActivity.class);
        startActivity(intent);

        Sender.getInstance().s_create(new GameRoomDef("-", geo));
	}

	//////////////////////////// Location Listener /////////////////////////////////////
	@Override
	public void onLocationChanged(Location location) {

        locationSet = true;
        geo = new GeoLocation(location.getLatitude(), location.getLongitude());

        try {
            Sender.getInstance().s_listGamesRequest(geo);
        } catch ( TException e ) {
            Log.e("ListGameRoom", e.getMessage() );
        }
	}

	@Override public void onProviderDisabled(String a) { }
	@Override public void onProviderEnabled(String a) { }
	@Override public void onStatusChanged(String a, int b, Bundle c) { }

    private class ApiDelegate extends Api {
        @Override public void c_listGamesResponse(final List<GameRoom> rooms) throws TException {
            runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    rooms_.clear();
                    for( GameRoom room : rooms ) {
                        rooms_.add( new GameRoomAdapter( room ) );
                    }
                    adapter_.notifyDataSetChanged();
                }
            });
        }
    }

    private class GameRoomAdapter {
        public GameRoomAdapter( GameRoom room ) {
            this.room = room;
        }

        @Override public String toString() {
            return "Room " + room.definition.name + " " + room.definition.geo.latitude + " " + room.definition.geo.longitude;
        }

        public GameRoom room;
    }

    private static ArrayAdapter adapter_;
    private static ArrayList<GameRoomAdapter> rooms_ = new ArrayList<GameRoomAdapter>();
    private LocationManager locationManager_;

    private boolean locationSet = false;
    private GeoLocation geo = new GeoLocation(0,0);
    private ListGameRoomActivity self = this;
}
