package ca.polymtl.inf8405.sevenwonders.model;

import ca.polymtl.inf8405.sevenwonders.api.Player;
import java.util.HashMap;

public class PlayerInfo extends HashMap<String, String>{

	private Player player_;
	public PlayerInfo( Player player ){ player_ = player; }
	
	public static String KEY_NAME = "name";
	public static String KEY_CIVILISATION = "civilisation";
	public static String KEY_COINS = "coins";
	public static String KEY_WONDER_STAGE = "wonderStage";
	public static String KEY_BATTLE_MARKERS = "battleMarkers";
	public static String KEY_SCORE = "score";
	
	@Override
	public String get(Object k) {
	  String key = (String) k;
	  
	  if (key.equals(KEY_BATTLE_MARKERS))
		  return "WIP";
	  if (key.equals(KEY_CIVILISATION))
		  return player_.civilisation.name();
	  if (key.equals(KEY_COINS))
		  return player_.coins+"";
	  if (key.equals(KEY_NAME))
		  return "My name";
	  if (key.equals(KEY_WONDER_STAGE))
		  return player_.wonderStaged+"";
	  if (key.equals(KEY_SCORE))
		  return player_.score+"";
	  
	  return null;
	}
}
