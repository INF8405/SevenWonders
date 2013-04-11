package ca.polymtl.inf8405.sevenwonders;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import ca.polymtl.inf8405.sevenwonders.api.GameRoom;
import ca.polymtl.inf8405.sevenwonders.api.GameRoomDef;
import ca.polymtl.inf8405.sevenwonders.api.GeoLocation;

import org.apache.thrift.TException;

import java.util.List;

public class GeoLocateActivity extends Activity implements LocationListener{

	private TextView locationField;
	private LocationManager locationManager;
	private String provider;
    private Receiver receiver;

    public GeoLocateActivity(){
        receiver = new Receiver( this );
        receiver.start();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_locate);
		
		locationField = (TextView) findViewById(R.id.Position);

	    // Get the location manager
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    // Define the criteria how to select the locatioin provider -> use
	    // default
	    Criteria criteria = new Criteria();
	    provider = locationManager.getBestProvider(criteria, false);
	    Location location = locationManager.getLastKnownLocation(provider);

	    // Initialize the location fields
	    if (location != null) {
	      System.out.println("Provider " + provider + " has been selected.");
	      onLocationChanged(location);
	    } else {
	      locationField.setText("Location not available");
	    }
	}

    public void updateGameList(final List<GameRoom> rooms) {

        TextView view = (TextView) findViewById(R.id.allGames);
        view.clearComposingText();
        view.setText("");
        for( GameRoom room : rooms ) {
            view.append( room.getId() );
            view.append( "\n" );
        }
    }

    public void createGame( View view ) throws TException {
        Sender.getInstance().client.s_create( new GameRoomDef( "allo", new GeoLocation( "1","1" ) ) );
    }

    public void listGames( View view ) throws TException {
        Sender.getInstance().client.s_listGamesRequest(new GeoLocation("1", "1"));
    }

	@Override
	public void onLocationChanged(Location location) {
	    locationField.setText("Your current location (long-lat) = " +
	    		String.valueOf(location.getLongitude()) + " ; " 
	    		+ String.valueOf(location.getLatitude()));
	}

	@Override
	public void onProviderDisabled(String arg0) {
	}

	@Override
	public void onProviderEnabled(String arg0) {
		Toast.makeText(this, "Enabled new provider " + provider,
		        Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		Toast.makeText(this, "Disabled provider " + provider,
		        Toast.LENGTH_SHORT).show();
	}
}
