package ca.polymtl.inf8405.sevenwonders;

import ca.polymtl.inf8405.sevenwonders.api.*;
import org.apache.thrift.TException;

import java.util.List;
import java.util.Map;

public class Api implements SevenWondersApi.Iface {

    @Override public void c_listGamesResponse(List<GameRoom> rooms) throws TException {}
    @Override public void c_joined(String user) throws TException {}
    @Override public void c_connected(List<String> users) throws TException {}
    @Override public void c_left(String user) throws TException {}
    @Override public void c_begin(GameState state) throws TException {}
    @Override public void c_sendState(GameState state) throws TException {}
    @Override public void c_sendEndState(GameState state, List<Map<String, Integer>> detail) throws TException {}
    @Override public void c_ping() throws TException {}

    // Server calls this should not be implemented
    @Override final public void s_listGamesRequest(GeoLocation geo) throws TException {}
    @Override final public void s_create(GameRoomDef definition) throws TException {}
    @Override final public void s_join(String id) throws TException { }
    @Override final public void s_start() throws TException { }
    @Override final public void s_playCard(Card card, Map<Resource, List<NeighborReference>> trade) throws TException { }
    @Override final public void s_playWonder(Map<Resource, List<NeighborReference>> trade) throws TException { }
    @Override final public void s_discard(Card card) throws TException { }
    @Override final public void s_pong() throws TException { }
}