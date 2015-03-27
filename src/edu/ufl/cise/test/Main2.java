package edu.ufl.cise.test;

import java.io.IOException;
import java.util.LinkedHashMap;

public class Main2 {

	public static void main(String args[]) throws IOException{
		// Initialize the map with peer info
		State state1 = new State(8080);
		State state2 = new State(9090);
		State state3 = new State(9999);
		LinkedHashMap<Integer, State> map = new LinkedHashMap<Integer, State>();
		map.put(1, state1);
		map.put(2, state2);
		map.put(3, state3);
		PeerInfo.getInstance().setMap(map);
		PeerInfo.getInstance().setPeerId(2);
		PeerInfo.getInstance().setPortNumber(9090);

		// Start executor service
		ExecutorPool.getInstance().init(2);

		// Start a server
		PeerInfo.getInstance().Serverinit();

		// Start a client for host3.
		PeerInfo.getInstance().clientInit();
	}
	
}
