package ca.polymtl.inf8405.sevenwonders;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import ca.polymtl.inf8405.sevenwonders.api.GameRoom;
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
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.google.android.gms.maps.MapFragment;

import java.util.*;

public class ListGameRoomActivity extends Activity implements LocationListener{

	public static final String CONNECTED_MESSAGE = "ListGameRoomActivity_GameId";
	private static final int UPDATE_INTERVAL = (1000 * 60) /* minutes >> */ * 1;

    private LocationManager locationManager_;

    private boolean locationIsSet = false;
    private GeoLocation geo = new GeoLocation(0,0);
	private GoogleMap map;
    private Set<GameRoom> rooms_ = new HashSet<GameRoom>();
    private Map<String,GameRoom> markerRoomAssociation = new HashMap<String,GameRoom>();

	public ListGameRoomActivity() {
        if (!MainActivity.DEBUG_MODE){
		    Receiver.getInstance().addObserver( new ApiDelegate() );
        }
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_game_rooms);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		locationManager_ = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Define the criteria how to select the locatioin provider -> use default
		Criteria criteria = new Criteria();
		String provider = locationManager_.getBestProvider(criteria, false);
		locationManager_.requestLocationUpdates(provider, UPDATE_INTERVAL, 0, this);

		Location location = locationManager_.getLastKnownLocation(provider);

		if (null != location) {
			onLocationChanged(location); 
		} else {
			locationIsSet = false;
		}
	}

	//////////////////////////// Location Listener /////////////////////////////////////
	@Override
	public void onLocationChanged(Location location) {

        geo = new GeoLocation(location.getLatitude(), location.getLongitude());

        if( !locationIsSet) {
            locationIsSet = true;

            if (!MainActivity.DEBUG_MODE){
                try {
                    Sender.getInstance().s_listGamesRequest(geo);
                } catch ( TException e ) {
                    Log.e("ListGameRoom", e.getMessage() );
                }
            }

            final LatLng position = new LatLng(location.getLatitude(),location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLng(position));

            final Marker createGameMarker = map.addMarker(
                new MarkerOptions().
                    position(position).
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
            );

            map.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if( marker.getId().equals(createGameMarker.getId())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ListGameRoomActivity.this);
                        builder.setMessage("Create a new game?")
                                .setPositiveButton("Create", createGame)
                                .setNegativeButton("Cancel", createGame)
                                .show();
                    } else {
                        final GameRoom room = markerRoomAssociation.get(marker.getId());
                        if( null != room ) {
                            DialogInterface.OnClickListener joinGame = new JoinRoomListener(room);
                            AlertDialog.Builder builder = new AlertDialog.Builder(ListGameRoomActivity.this);
                            builder.setMessage( room.definition.name + " Join game?")
                                    .setPositiveButton("Join", joinGame)
                                    .setNegativeButton("Cancel", joinGame)
                                    .show();
                        }
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

                    Intent intent = new Intent(ListGameRoomActivity.this, GameRoomActivity.class);
                    intent.putExtra(CONNECTED_MESSAGE, new ArrayList<String>().toArray() );
                    startActivity(intent);

                    if (!MainActivity.DEBUG_MODE){
                        try {
                            Sender.getInstance().s_create(new GameRoomDef("-", geo));
                        } catch ( TException e ) {
                            Log.e( "ListGameRoomActivity", e.getMessage() );
                        }
                    }

                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
	};

	private class JoinRoomListener implements DialogInterface.OnClickListener {

	    private GameRoom room_;
	    public JoinRoomListener( GameRoom room ) {
	        room_ = room;
	    }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch (i){
                case DialogInterface.BUTTON_POSITIVE:
                    if (!MainActivity.DEBUG_MODE){
                        try {
                            Sender.getInstance().s_join(room_.id);
                        } catch ( TException e ) {
                            Log.e( "ListGameRoomActivity", e.getMessage() );
                        }
                    }
                break;

                case DialogInterface.BUTTON_NEGATIVE:
                break;
            }
        }
    }

    public void onProviderDisabled(String provider) {
		// Switch between GPS provider and Network provider
		if (provider.equals(LocationManager.GPS_PROVIDER)){
			provider = LocationManager.NETWORK_PROVIDER;
			locationManager_.requestLocationUpdates(provider, UPDATE_INTERVAL, 0, this);
		}
		else {
			provider = LocationManager.GPS_PROVIDER;
			locationManager_.requestLocationUpdates(provider, UPDATE_INTERVAL, 0, this);
		}
		locationManager_.getLastKnownLocation(provider);
	}
	public void onProviderEnabled(String provider) { 	}
	public void onStatusChanged(String provider, int status, Bundle extra) { 	}

	private class ApiDelegate extends Api {

        @Override public void c_createdGame() throws TException {
            if(locationIsSet) {
                Sender.getInstance().s_listGamesRequest(geo);
            }
        }

		@Override
		public void c_listGamesResponse(final List<GameRoom> rooms) throws TException {
			runOnUiThread( new Runnable() {
				@Override
				public void run() {
                    for( final GameRoom room : rooms ) {
                        if( !rooms_.contains( room ) ) {
                            final GameRoom nRoom = room.deepCopy();
                            final GeoLocation geo = nRoom.definition.geo;
                            final Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(geo.latitude, geo.longitude)));

                            markerRoomAssociation.put(marker.getId(), nRoom);
                            rooms_.add(nRoom);
                        }
                    }
				}
			});
		}

        @Override
        public void c_connected(final List<String> users) throws TException {
            runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(ListGameRoomActivity.this, GameRoomActivity.class);
                    intent.putExtra(CONNECTED_MESSAGE,users.toArray());
                    startActivity(intent);
                }
            });
        }
    }
}
