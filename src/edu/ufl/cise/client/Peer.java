package edu.ufl.cise.client;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import edu.ufl.cise.config.PeerInfo;
import edu.ufl.cise.server.Server;

public class Peer {

	private static volatile Peer instance;
	private int peerId;
	private int portNumber;
	int currentOptimisticUnchoked;
	int numPeers;
	int numPeersCompleted;
	private LinkedHashMap<Integer, PeerInfo> map;
	private HashMap<Integer, Integer> piecesCurrentlyDownloading;  // Maps pieces to the peer Id
	private HashMap<Integer, Boolean> currentlyInterested;   // Peers currently interested in me. true if interested
	private HashMap<Integer, Boolean> unchokedMap;  // Peers who has currently unchoked me. true if unchoked
	private HashMap<Integer, Boolean> interestedSent; // Peers to whom interested is sent. true if interested sent
	private HashMap<Integer, Boolean> preferredNeighbors; // Current preferred neighbors
	private HashMap<String, Integer> hostNameToIdMap;  // For checking which peerId this connection request belongs to when
													   //  a connection request comes.

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public static Peer getInstance() {
		if (instance == null) {
			synchronized (Peer.class) {
				if (instance == null)
					instance = new Peer();
			}
		}
		return instance;
	}

	private Peer() {
	}

	public void init(int peerId, LinkedHashMap<Integer, PeerInfo> peerMap) {
		this.peerId = peerId;
		this.map = peerMap;
	}
	
	public int getPeerId() {
		return peerId;
	}

	public void setPeerId(int peerId) {
		this.peerId = peerId;
	}

	public LinkedHashMap<Integer, PeerInfo> getMap() {
		return map;
	}

	public void setMap(LinkedHashMap<Integer, PeerInfo> map) {
		this.map = map;
	}
	
	public void updateClientSocket(int peerId, Socket socket){
		String peerIdString = peerId + "";
		PeerInfo peerInfo = map.get(peerIdString);
		peerInfo.setSocket(socket);
	}

	public void Serverinit() throws IOException {
		Server server = new Server(portNumber);
		new Thread(server).start();;
	}

	/**
	 * Sends Handshake messages to each peer before it.
	 */
	public void clientInit() {
		Iterator<Integer> itr = map.keySet().iterator();
		while (itr.hasNext()) {
			int peerId1 = itr.next();
			PeerInfo peerInfo = map.get(peerId1);
			if (peerId1 > peerId)
				continue;
			else if (peerId1 == peerId) {
				portNumber = peerInfo.getPort();
			} else {
				String hostName = peerInfo.getHostname();
				int port = peerInfo.getPort();
				//Client.init(peerId1, hostName, port);
				ClientWorker worker = new ClientWorker(peerId1, port, hostName);
				new Thread(worker).start();
			}
		}
	}

	public int getCurrentOptimisticUnchoked() {
		return currentOptimisticUnchoked;
	}

	public void setCurrentOptimisticUnchoked(int currentOptimisticUnchoked) {
		this.currentOptimisticUnchoked = currentOptimisticUnchoked;
	}

	public int getNumPeers() {
		return numPeers;
	}

	public void setNumPeers(int numPeers) {
		this.numPeers = numPeers;
	}

	public int getNumPeersCompleted() {
		return numPeersCompleted;
	}

	public void setNumPeersCompleted(int numPeersCompleted) {
		this.numPeersCompleted = numPeersCompleted;
	}

	public HashMap<Integer, Integer> getPiecesCurrentlyDownloading() {
		return piecesCurrentlyDownloading;
	}

	public void setPiecesCurrentlyDownloading(
			HashMap<Integer, Integer> piecesCurrentlyDownloading) {
		this.piecesCurrentlyDownloading = piecesCurrentlyDownloading;
	}

	public HashMap<Integer, Boolean> getCurrentlyInterested() {
		return currentlyInterested;
	}

	public void setCurrentlyInterested(HashMap<Integer, Boolean> currentlyInterested) {
		this.currentlyInterested = currentlyInterested;
	}

	public HashMap<Integer, Boolean> getUnchokedMap() {
		return unchokedMap;
	}

	public void setUnchokedMap(HashMap<Integer, Boolean> unchokedMap) {
		this.unchokedMap = unchokedMap;
	}

	public HashMap<Integer, Boolean> getInterestedSent() {
		return interestedSent;
	}

	public void setInterestedSent(HashMap<Integer, Boolean> interestedSent) {
		this.interestedSent = interestedSent;
	}

	public HashMap<Integer, Boolean> getPreferredNeighbors() {
		return preferredNeighbors;
	}

	public void setPreferredNeighbors(HashMap<Integer, Boolean> preferredNeighbors) {
		this.preferredNeighbors = preferredNeighbors;
	}

	public HashMap<String, Integer> getHostNameToIdMap() {
		return hostNameToIdMap;
	}

	public void setHostNameToIdMap(HashMap<String, Integer> hostNameToIdMap) {
		this.hostNameToIdMap = hostNameToIdMap;
	}

}
