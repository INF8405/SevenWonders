package ca.polymtl.inf8405.sevenwonders;

import android.util.Log;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.*;

import ca.polymtl.inf8405.sevenwonders.api.Config;

public class ServerApi {

    private static ServerApi instance = new ServerApi();

    private ServerApi(){
        try {
            transport.open();
        } catch( TTransportException e ) {
            Log.e("ServerApi", e.getMessage() );
        }
    }
    public static ServerApi getInstance(){
        return instance;
    }

	public final TTransport transport = new TSocket( Config.ip, Config.port );
	public final TProtocol protocol = new TBinaryProtocol(transport);
}