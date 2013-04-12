namespace java ca.polymtl.inf8405.sevenwonders.api

enum Resource {
    CLAY = 0,
    WOOD = 1,
    ORE = 2,
    STONE = 3,
    GLASS = 4,
    PAPER = 5,
    TAPESTRY = 6
}

enum NeighborReference {
    LEFT = 0,
    RIGHT = 1
}

enum CardCategory {
    SCIENCE = 0,
    MILITARY = 1,
    RAW_MATERIAL = 2,
    MANUFACTURED_GOOD = 3,
    CIVILIAN = 4,
    COMMERCIAL = 5,
    GUILD = 6
}

enum Civilisation {
    RHODES_A = 0,
    RHODES_B = 1,
    ALEXANDRIA_A = 2,
    ALEXANDRIA_B = 3,
    EPHESUS_A = 4,
    EPHESUS_B = 5,
    BABYLON_A = 6,
    BABYLON_B = 7,
    OLYMPIA_A = 8,
    OLYMPIA_B = 9,
    HALICARNASSUS_A = 10,
    HALICARNASSUS_B = 11,
    GIZAH_A = 12,
    GIZAH_B = 13
}

enum Card {

    /* === Age I === */

    // Commercial
    TAVERN = 0,
    WEST_TRADING_POST = 1,
    MARKETPLACE = 2,
    EAST_TRADING_POST = 3,

    // Military
    STOCKADE = 4,
    BARRACKS = 5,
    GUARD_TOWER = 6,

    // Science
    WORKSHOP = 7,
    SCRIPTORIUM = 8,
    APOTHECARY = 9,

    // Civilian
    THEATER = 10,
    BATHS = 11,
    ALTAR = 12,
    PAWNSHOP = 13,

    // Raw Material
    TREE_FARM = 14,
    MINE = 15,
    CLAY_PIT = 16,
    TIMBER_YARD = 17,
    STONE_PIT = 18,
    FOREST_CAVE = 19,
    LUMBER_YARD = 20,
    ORE_VEIN = 21,
    EXCAVATION = 22,
    CLAY_POOL = 23,

    // Manufactured Good
    LOOM = 24,
    GLASSWORKS = 25,
    PRESS = 26,

    /* === Age II === */

    // Commercial
    CARAVANSERY = 27,
    FORUM = 28,
    BAZAR = 29,
    VINEYARD = 30,

    // Military
    WALLS = 31,
    ARCHERY_RANGE = 32,
    TRAINING_GROUND = 33,
    STABLES = 34,

    // Science
    SCHOOL = 35,
    LIBRARY = 36,
    LABORATORY = 37,
    DISPENSARY = 38,

    // Civilian
    AQUEDUCT = 39,
    STATUE = 40,
    TEMPLE = 41,
    COURTHOUSE = 42,

    // Raw Material
    FOUNDRY = 43,
    QUARRY = 44,
    BRICKYARD = 45,
    SAWMILL = 46,

    /* === Age III === */

    // Commercial
    ARENA = 47,
    CHAMBER_OF_COMMERCE = 48,
    LIGHTHOUSE = 49,
    HAVEN = 50,

    // Military
    CIRCUS = 51,
    FORTIFICATIONS = 52,
    ARSENAL = 53,
    SIEGE_WORKSHOP = 54,

    // Science
    OBSERVATORY = 55,
    ACADEMY = 56,
    LODGE = 57,
    UNIVERSITY = 58,
    STUDY = 59,

    // Civilian
    TOWN_HALL = 60,
    PALACE = 61,
    PANTHEON = 62,
    GARDENS = 63,
    SENATE = 64,

    // Guilds
    STRATEGISTS_GUILD = 65,
    TRADERS_GUILD = 66,
    MAGISTRATES_GUILD = 67,
    SHIPOWNERS_GUILD = 68,
    CRAFTMENS_GUILD = 69,
    WORKERS_GUILD = 70,
    PHILOSOPHERS_GUILD = 71,
    SCIENTISTS_GUILD = 72,
    SPIES_GUILD = 73,
    BUILDERS_GUILD = 74
}

typedef string GameId
typedef string Username
typedef list<map<string,i32>> ScoreDetail
typedef i32 BattleMarker
typedef map<Resource,list<NeighborReference>> Trade

struct GeoLocation {
	1: double latitude,
	2: double longitude
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
	2: Civilisation civilisation,
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
    oneway void c_connected( 1: list<Username> users ),
	oneway void c_left( 1: Username user ),
	oneway void s_start( ),

    oneway void c_begin( 1: GameState state ),
	oneway void c_sendState( 1: GameState state ),

	oneway void s_playCard( 1: Card card, 2: Trade trade ),
	oneway void s_playWonder( 1: Trade trade ),
	oneway void s_discard( 1: Card card ),

	oneway void c_sendEndState( 1: GameState state, 2: ScoreDetail detail )

	oneway void s_pong( );
	oneway void c_ping( );
}