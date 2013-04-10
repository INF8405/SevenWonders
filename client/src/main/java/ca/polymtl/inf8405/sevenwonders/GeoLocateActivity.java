package ca.polymtl.inf8405.sevenwonders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GeoLocateActivity extends Activity implements LocationListener{

	private TextView locationField;
	private LocationManager locationManager;
	private String provider;

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
	
	    // Test
//	    Button button = (Button)findViewById(R.id.ButtonTest);
//	    button.setBackgroundColor(Color.GRAY);
//	    button.setText(Html.fromHtml("<font color='red'>First line</font><br/><font color='blue'>Second line</font>"));
	    
	    //
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
	    locationField.setText("Your current location (long-lat) = " +
	    		String.valueOf(location.getLongitude()) + " ; " 
	    		+ String.valueOf(location.getLatitude()));
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Enabled new provider " + provider,
		        Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Disabled provider " + provider,
		        Toast.LENGTH_SHORT).show();
	}
	
	public void joinGame(View view){
		// TODO: Send Join request to server and wait
		Intent intent = new Intent(this, ScreenSlidePagerActivity.class);
		startActivity(intent);
	}
	
	public void createGame(View view){
		// TODO: Send Create request to server and wait
		Intent intent = new Intent(this, ScreenSlidePagerActivity.class);
		startActivity(intent);
	}
	
}
