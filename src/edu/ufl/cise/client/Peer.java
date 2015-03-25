package edu.ufl.cise.client;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

import edu.ufl.cise.config.PeerInfo;
import edu.ufl.cise.protocol.HandshakeMessage;
import edu.ufl.cise.server.Server;

public class Peer {

	int peerId;
	int portNumber;
	LinkedHashMap<String, PeerInfo> map ;
	
	public Peer( int peerId, LinkedHashMap<String, PeerInfo> map ){
		this.peerId = peerId;
		this.map = map;
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

	public void Serverinit() throws IOException {
		Server server = new Server();
		server.init(portNumber);
	}

	/**
	 * 	Sends Handshake messages to each peer before it.
	 */
	public void clientInit() {
		Iterator<String> itr = map.keySet().iterator();
		while(itr.hasNext()){
			String peer = itr.next();
			PeerInfo peerInfo = map.get(peer);
			int peerId1 = Integer.parseInt(peer);
			if( peerId1 > peerId) continue;
			else if(peerId1 == peerId){
				portNumber = peerInfo.getPort();
			}
			else{
				String hostName = peerInfo.getHostname();
				int port = peerInfo.getPort();
				// Sends handshake message to peer
				HandshakeMessage message = new HandshakeMessage(peerId1);
				Client.sendMessage(message, hostName, port);
			}
		}
	}
	
}
