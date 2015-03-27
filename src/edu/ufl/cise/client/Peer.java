package edu.ufl.cise.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedHashMap;

import edu.ufl.cise.config.PeerInfo;
import edu.ufl.cise.protocol.HandshakeMessage;
import edu.ufl.cise.server.Server;

public class Peer {

	public static volatile Peer instance;
	int peerId;
	int portNumber;
	LinkedHashMap<String, PeerInfo> map;

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

	public void init(int peerId, LinkedHashMap<String, PeerInfo> peerMap) {
		this.peerId = peerId;
		this.map = peerMap;
	}
	
	public int getPeerId() {
		return peerId;
	}

	public void setPeerId(int peerId) {
		this.peerId = peerId;
	}

	public LinkedHashMap<String, PeerInfo> getMap() {
		return map;
	}

	public void setMap(LinkedHashMap<String, PeerInfo> map) {
		this.map = map;
	}
	
	public void updateClientSocket(int peerId, Socket socket){
		String peerIdString = peerId + "";
		PeerInfo peerInfo = map.get(peerIdString);
		peerInfo.setSocket(socket);
	}

	public void Serverinit() throws IOException {
		Server server = new Server();
		server.init(portNumber);
	}

	/**
	 * Sends Handshake messages to each peer before it.
	 */
	public void clientInit() {
		Iterator<String> itr = map.keySet().iterator();
		while (itr.hasNext()) {
			String peer = itr.next();
			PeerInfo peerInfo = map.get(peer);
			int peerId1 = Integer.parseInt(peer);
			if (peerId1 > peerId)
				continue;
			else if (peerId1 == peerId) {
				portNumber = peerInfo.getPort();
			} else {
				String hostName = peerInfo.getHostname();
				int port = peerInfo.getPort();
				//Client.init(peerId1, hostName, port);
				ClientWorker worker = new ClientWorker(peerId1, port, hostName);
				worker.run();
			}
		}
	}

}
