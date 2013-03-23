namespace java ca.polymtl.inf8405.sevenwonders.api

typedef i32 GameId
typedef string Username
typedef string Card
typedef list<map<string,i32>> ScoreDetail
typedef i32 BattleMarker
typedef string CardCategory

struct GeoLocation {
	1: string longitude,
	2: string latitude
}

struct GameRoomDef {
	1: string name,
	2: GeoLocation geo
}

struct GameRoom {
	1: GameId id,
	2: GameRoomDef definition
}

struct Hand {
	1: list<Card> playable,
	2: list<Card> unplayable
}

struct Player {
	1: map<CardCategory, list<Card>> tableau,
	2: string civilisation,
	3: list<BattleMarker> battleMarkers,
	4: i32 coins,
	5: i32 score,
	6: i32 wonderStaged
}

struct GameState {
	1: Hand hand,
	2: list<Player> player
}

service SevenWondersApi {

	list<GameRoom> s_listGames( 1: GeoLocation geo )
	oneway void s_create( 1: GameRoomDef definition );
	oneway void s_join( 1: GameId id );
	oneway void c_joined( 1: Username user );
	oneway void s_start( );

	oneway void c_sendState( 1: GameState state );

	oneway void s_playCard( 1: Card card );
	oneway void s_playWonder( );
	oneway void s_discard( 1: Card card );

	oneway void c_sendEndState( 1: GameState state, 2: ScoreDetail detail )
}