package ca.polymtl.inf8405.sevenwonders;

import ca.polymtl.inf8405.sevenwonders.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.Window;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
	}

	public void play(View view){
//		Intent intent = new Intent(this, GameScreen.class);
//		startActivity(intent);
	}
	
	public void test(View view){
		Intent intent = new Intent(this, ScreenSlidePagerActivity.class);
		startActivity(intent);
	}

}
