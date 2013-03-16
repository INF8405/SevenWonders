package ca.polymtl.inf8405.sevenwonders;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class TestActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_game_screen);
	}
}
