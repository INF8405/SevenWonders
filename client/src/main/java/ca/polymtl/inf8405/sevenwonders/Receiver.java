package ca.polymtl.inf8405.sevenwonders;

import ca.polymtl.inf8405.sevenwonders.api.*;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Receiver extends Api {

    private static Receiver instance = new Receiver();
    private Receiver() {
        start();
    }
    public static Receiver getInstance(){
        return instance;
    }

	private SevenWondersApi.Processor processor = new SevenWondersApi.Processor( this );

	private void start() {
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

    public void addObserver( Api observer ){
        observers.add( observer );
    }

	public void c_listGamesResponse(final List<GameRoom> rooms) throws TException {
        for( Api observer : observers ) {
            observer.c_listGamesResponse(rooms);
        }
	}

	public void c_joined(String user) throws TException {
        for( Api observer : observers ) {
            observer.c_joined(user);
        }
	}

    @Override public void c_connected(List<String> users) throws TException {
        for( Api observer : observers ) {
            observer.c_connected(users);
        }
    }

	public void c_left(String user) throws TException {
        for( Api observer : observers ) {
            observer.c_left(user);
        }
	}

	public void c_sendState(final GameState state) throws TException {
        for( Api observer : observers ) {
            observer.c_sendState( state );
        }
	}

	public void c_sendEndState(GameState state, List<Map<String,Integer>> detail) throws TException {
        for( Api observer : observers ) {
            observer.c_sendEndState( state, detail );
        }
	}

	public void c_ping() throws TException{
		Sender.getInstance().s_pong();
	}

    private List<Api> observers = new LinkedList<Api>();
}

