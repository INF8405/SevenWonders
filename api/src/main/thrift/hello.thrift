namespace java ca.polymtl.inf8405.sevenwonders.api

service HelloService {
    oneway void s_server( 1: string msg );
    oneway void c_client( 1: string msg );
}