namespace java ca.polymtl.inf8405.sevenwonders.api

enum Resource {
    Clay = 0,
    Wood = 1,
    Ore = 2,
    Stone = 3,
    Glass = 4,
    Paper = 5,
    Tapestry = 6
}

enum NeighborReference {
    Left = 0,
    Right = 1
}

typedef string GameId
typedef string Username
typedef string Card
typedef list<map<string,i32>> ScoreDetail
typedef i32 BattleMarker
typedef string CardCategory
typedef map<Resource,list<NeighborReference>> Trade

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
    1: map<Card,set<Trade>> playables,
	2: list<Card> unplayables
}

struct Player {
	1: map<CardCategory, list<Card>> tableau,
	2: string civilisation,
	3: list<BattleMarker> battleMarkers,
	4: i32 coins,
	5: i32 score,
	6: i32 wonderStaged,
	7: bool canPlayWonder
}

struct GameState {
	1: Hand hand,
	2: list<Player> players
}

service SevenWondersApi {

	oneway void s_listGamesRequest( 1: GeoLocation geo ),
    oneway void c_listGamesResponse( 1: list<GameRoom> rooms ),
	oneway void s_create( 1: GameRoomDef definition ),
	oneway void s_join( 1: GameId id ),
	oneway void c_joined( 1: Username user ),
	oneway void c_left( 1: Username user ),
	oneway void s_start( ),

	oneway void c_sendState( 1: GameState state ),

	oneway void s_playCard( 1: Card card, 2: Trade trade ),
	oneway void s_playWonder( 1: Trade trade ),
	oneway void s_discard( 1: Card card ),

	oneway void c_sendEndState( 1: GameState state, 2: ScoreDetail detail )

	oneway void s_pong( );
	oneway void c_ping( );
}