package edu.ufl.cise.test;

import java.io.IOException;
import java.util.HashMap;
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
		
		HashMap<String, Integer> hostToIdMap = new HashMap<String, Integer>();
		//hostToIdMap.put("localhost", 1);
		hostToIdMap.put("localhost", 2);
		//hostToIdMap.put("localhost", 3);
		PeerInfo.getInstance().setHostNameToIdMap(hostToIdMap);
		
		PeerInfo.getInstance().setMap(map);
		PeerInfo.getInstance().setPeerId(2);
		PeerInfo.getInstance().setPortNumber(9090);
		PeerInfo.getInstance().setHostName("lin116-01.cise.ufl.edu");
		PeerInfo.getInstance().setHostName("localhost");
		
		// Start executor service
		ExecutorPool.getInstance().init(2);

		// Start a server
		PeerInfo.getInstance().Serverinit();

		// Start a client for host3.
		PeerInfo.getInstance().clientInit();
	}
	
}
