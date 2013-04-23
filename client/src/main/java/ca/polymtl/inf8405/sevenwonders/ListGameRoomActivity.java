package ca.polymtl.inf8405.sevenwonders;


import android.app.AlertDialog;
import android.content.DialogInterface;
import ca.polymtl.inf8405.sevenwonders.api.GameRoomDef;
import ca.polymtl.inf8405.sevenwonders.api.GeoLocation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.apache.thrift.TException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.google.android.gms.maps.MapFragment;

public class ListGameRoomActivity extends Activity implements LocationListener{

	public static final String CONNECTED_MESSAGE = "ListGameRoomActivity_GameId";
	private static final int FIVE_MINUTES = 1000 * 60 * 5;

    private LocationManager locationManager_;

    private boolean locationSet = false;
    private GeoLocation geo = new GeoLocation(0,0);
	private GoogleMap map;

	public ListGameRoomActivity() {
//        if (!MainActivity.DEBUG_MODE){
//		    Receiver.getInstance().addObserver( new ApiDelegate() );
//        }
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_game_rooms);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

//		USER_NAME =  getIntent().getStringExtra(LogInActivity.USER_NAME_MESSAGE);

		// Create a list view of game room
//		final ListView listView = (ListView) findViewById(R.id.listView);
//		adapter_ = new ArrayAdapter<GameRoomAdapter>(this, R.layout.gameroom_item,rooms_);
//		listView.setAdapter(adapter_);
//		listView.setTextFilterEnabled(true);
//		listView.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (!MainActivity.DEBUG_MODE){
//                    try {
//                        Sender.getInstance().s_join( rooms_.get(position).room.id );
//                    } catch ( TException e ) {
//                        Log.e( "ListGameRoomActivity", e.getMessage() );
//                    }
//                }
//            }
//		});

		locationManager_ = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Define the criteria how to select the locatioin provider -> use default
		Criteria criteria = new Criteria();
		String provider = locationManager_.getBestProvider(criteria, false);
		locationManager_.requestLocationUpdates(provider, FIVE_MINUTES, 0, this);

		Location location = locationManager_.getLastKnownLocation(provider);

		if (location != null) {
			onLocationChanged(location); 
		} else {
			locationSet = false;
		}
	}

	//////////////////////////// Location Listener /////////////////////////////////////
	@Override
	public void onLocationChanged(Location location) {

        geo = new GeoLocation(location.getLatitude(), location.getLongitude());

        if( !locationSet ) {
            locationSet = true;

            if (!MainActivity.DEBUG_MODE){
                try {
                    Sender.getInstance().s_listGamesRequest(geo);
                } catch ( TException e ) {
                    Log.e("ListGameRoom", e.getMessage() );
                }
            }

            final LatLng position = new LatLng(location.getLatitude(),location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLng(position));

            // Debug
            map.addMarker(new MarkerOptions().position(new LatLng(45.46, -73.63)));

            final Marker createGameMarker = map.addMarker(
                new MarkerOptions().
                    position(position).
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
            );

            map.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if( marker.equals(createGameMarker)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ListGameRoomActivity.this);
                        builder.setMessage("Create a new game?")
                                .setPositiveButton("Create", createGame)
                                .setNegativeButton("Cancel", createGame)
                                .show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ListGameRoomActivity.this);
                        builder.setMessage( marker.getTitle() + " Join game?")
                                .setPositiveButton("Join", joinGame)
                                .setNegativeButton("Cancel", joinGame)
                                .show();
                    }

                    return true;
                }
            });
        }
	}

    DialogInterface.OnClickListener createGame = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
//                    Intent intent = new Intent(this, GameRoomActivity.class);
//                    intent.putExtra(CONNECTED_MESSAGE, new ArrayList<String>().toArray() );
//                    startActivity(intent);
//
//                    if (!MainActivity.DEBUG_MODE){
//                        Sender.getInstance().s_create(new GameRoomDef("-", geo));
//                    }

                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    DialogInterface.OnClickListener joinGame = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
//                if (!MainActivity.DEBUG_MODE){
//                    try {
//                        Sender.getInstance().s_join( rooms_.get(position).room.id );
//                    } catch ( TException e ) {
//                        Log.e( "ListGameRoomActivity", e.getMessage() );
//                    }
//                }

                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };


    @Override public void onProviderDisabled(String provider) {
		// Switch between GPS provider and Network provider
		if (provider.equals(LocationManager.GPS_PROVIDER)){
			provider = LocationManager.NETWORK_PROVIDER;
			locationManager_.requestLocationUpdates(provider, FIVE_MINUTES, 0, this);
		}
		else {
			provider = LocationManager.GPS_PROVIDER;
			locationManager_.requestLocationUpdates(provider, FIVE_MINUTES, 0, this);
		}
		locationManager_.getLastKnownLocation(provider);
	}
	@Override public void onProviderEnabled(String provider) { 	}
	@Override public void onStatusChanged(String provider, int status, Bundle extra) { 	}

//	private class ApiDelegate extends Api {
//
//        @Override public void c_createdGame() throws TException {
//            Sender.getInstance().s_listGamesRequest(geo);
//        }
//
//		@Override
//		public void c_listGamesResponse(final List<GameRoom> rooms) throws TException {
//			runOnUiThread( new Runnable() {
//				@Override
//				public void run() {
//					rooms_.clear();
//					for( GameRoom room : rooms ) {
//						rooms_.add( new GameRoomAdapter( room ) );
//					}
//					adapter_.notifyDataSetChanged();
//				}
//			});
//		}
//
//        @Override
//        public void c_connected(final List<String> users) throws TException {
//            runOnUiThread( new Thread( new Runnable() {
//                @Override
//                public void run() {
//                    Intent intent = new Intent(self, GameRoomActivity.class);
//                    intent.putExtra(CONNECTED_MESSAGE,users.toArray());
//                    startActivity(intent);
//                }
//            }));
//        }
//    }
}
