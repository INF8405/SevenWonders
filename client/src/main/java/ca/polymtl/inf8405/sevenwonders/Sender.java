package ca.polymtl.inf8405.sevenwonders;

import ca.polymtl.inf8405.sevenwonders.api.SevenWondersApi;

public class Sender{

    private Sender(){};

	private static Sender instance = new Sender();
    public static SevenWondersApi.Client getInstance() {
		return instance.client;
	}

    public SevenWondersApi.Client client = new SevenWondersApi.Client(ServerApi.getInstance().protocol);
}