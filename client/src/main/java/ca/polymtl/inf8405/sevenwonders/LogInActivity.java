package ca.polymtl.inf8405.sevenwonders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class LogInActivity extends Activity {
	private EditText userNameBox_;
	public static String USER_NAME_MESSAGE="userName";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_log_in);
		
		
		addKeyListener();
	}

	public void addKeyListener() {
		// get edittext component
		userNameBox_ = (EditText) findViewById(R.id.user_name);

		// add a keylistener to keep track user input
		userNameBox_.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// if keydown and "enter" is pressed
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// display a floating message
					Toast.makeText(LogInActivity.this,
							userNameBox_.getText(), Toast.LENGTH_LONG).show();
					
					Intent intent = new Intent(LogInActivity.this, ListGameRoomActivity.class);
					intent.putExtra(USER_NAME_MESSAGE, userNameBox_.getText().toString());
					startActivity(intent);
					finish();
					return true;
				}
				return false;
			}
		});
	}
}