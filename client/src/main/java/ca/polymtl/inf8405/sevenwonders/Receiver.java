package ca.polymtl.inf8405.sevenwonders;

import ca.polymtl.inf8405.sevenwonders.api.*;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import java.util.List;
import java.util.Map;

public class Receiver implements SevenWondersApi.Iface {

    public Receiver( GeoLocateActivity targetActivity ) {
        this.targetActivity = targetActivity;
    }

	private SevenWondersApi.Processor processor = new SevenWondersApi.Processor( this );

	public void start() {
		new Thread(new Runnable(){
			public void run(){
                try {
                    TProtocol protocol = ServerApi.getInstance().protocol;
                    while(true) {
                        processor.process( protocol, protocol );
                    }
                } catch (TException e) {
                    e.printStackTrace();
                }
			}
		}).start();
	}

	public void c_listGamesResponse(final List<GameRoom> rooms) throws TException {
        targetActivity.runOnUiThread( new Runnable() {
            @Override
            public void run() {
                targetActivity.updateGameList( rooms );
            }
        });
	}

	public void c_joined(String user) throws TException {

	}

	public void c_left(String user) throws TException {

	}

	public void c_sendState(final GameState state) throws TException {
		targetActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // update
            }
        });
	}

	public void c_sendEndState(GameState state, List<Map<String,Integer>> detail) throws TException {

	}

	public void c_ping() throws TException{
		Sender.getInstance().client.s_pong();
	}

	public void s_listGamesRequest(GeoLocation geo) throws TException {}
    public void s_create(GameRoomDef definition) throws TException {}
    public void s_join(String id) throws TException {}
    public void s_start() throws TException {}
    public void s_playCard(String card, Map<Resource,List<NeighborReference>> trade) throws TException {}
    public void s_playWonder(Map<Resource,List<NeighborReference>> trade) throws TException {}
    public void s_discard(String card) throws TException {}
    public void s_pong() throws TException {}

    private GeoLocateActivity targetActivity;
}