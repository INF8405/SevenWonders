package ca.polymtl.inf8405.sevenwonders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.thrift.TException;

public class LogInActivity extends Activity {
    private String username = "";
	public static String USER_NAME_MESSAGE="userName";

	public LogInActivity(){
        Receiver.getInstance().addObserver( new ApiDelegate() );
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_log_in);
		addKeyListener();
	}

	public void addKeyListener() {
		// get edittext component
        final EditText userNameBox_ = (EditText) findViewById(R.id.user_name);

		// add a keylistener to keep track user input
		userNameBox_.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// if keydown and "enter" is pressed
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    try {
                        username = userNameBox_.getText().toString();
                        Sender.getInstance().s_connect( username );
                    } catch ( TException e ) {
                        Log.wtf("login", e.getMessage());
                    }

					return true;
				}
				return false;
			}
		});
	}

    private class ApiDelegate extends Api {
        @Override public void c_connectionResponse(final boolean connected) throws TException {
            runOnUiThread( new Thread( new Runnable() {
                @Override
                public void run() {
                    if( connected ) {
                        Intent intent = new Intent(LogInActivity.this, ListGameRoomActivity.class);
                        intent.putExtra(USER_NAME_MESSAGE, username);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LogInActivity.this, "connection failed: " + username,
                            Toast.LENGTH_LONG).show();
                    }
                }
            }));
        }
    }
}