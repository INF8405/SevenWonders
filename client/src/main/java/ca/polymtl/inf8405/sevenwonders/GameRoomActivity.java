package ca.polymtl.inf8405.sevenwonders;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.thrift.TException;

public class GameRoomActivity extends Activity{

    public GameRoomActivity() {
        Receiver.getInstance().addObserver( new ApiDelegate() );
    }

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_game_room);

		// Create a list view of game room
		final ListView listView = (ListView) findViewById(R.id.listView);
		adapter_ = new ArrayAdapter<String>(this, R.layout.gameroom_item,players_);
		listView.setAdapter(adapter_);
		listView.setTextFilterEnabled(true);

        String gameid =  getIntent().getStringExtra(ListGameRoomActivity.GAMEID_MESSAGE);
        if( !gameid.equals("") ){
            try {
                Sender.getInstance().s_join( gameid );
            } catch ( TException e ) {
                Log.e( "ListGameRoomActivity", e.getMessage() );
            }
        }
	}
	
	public void play(View view) {
        Intent intent = new Intent(this, GameScreenActivity.class);
        startActivity(intent);
	}

    private class ApiDelegate extends Api {
        @Override public void c_joined(final String user) throws TException {
            runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    players_.add(user);
                    adapter_.notifyDataSetChanged();
                }
            });
        }
        @Override public void c_connected(final List<String> users) throws TException {
            runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    for( String user: users ){
                        players_.add(user);
                    }
//                    players_.addAll(users);
                    adapter_.notifyDataSetChanged();
                }
            });
        }
    }

    private static ArrayAdapter adapter_;
    private static ArrayList<String> players_ = new ArrayList<String>();
}
