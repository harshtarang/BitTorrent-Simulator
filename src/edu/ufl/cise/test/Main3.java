package edu.ufl.cise.test;

import java.io.IOException;
import java.util.LinkedHashMap;


public class Main3 {

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
		PeerInfo.getInstance().setPeerId(3);
		PeerInfo.getInstance().setPortNumber(9999);
		PeerInfo.getInstance().setHostName("lin116-03.cise.ufl.edu");
		PeerInfo.getInstance().setHostName("localhost");
		
		// Start executor service
		ExecutorPool.getInstance().init(3);

		// Start a server
		PeerInfo.getInstance().Serverinit();

		// Start a client on host2 and host3.
		PeerInfo.getInstance().clientInit();
	}
	
}
