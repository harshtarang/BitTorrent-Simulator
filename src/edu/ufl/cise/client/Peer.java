package edu.ufl.cise.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import edu.ufl.cise.config.PeerInfo;
import edu.ufl.cise.protocol.HandshakeMessage;
import edu.ufl.cise.server.Server;

public class Peer {

	int peerId;
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

	public void setList(ArrayList<PeerInfo> list) {
		this.list = list;
	}

	public void Serverinit() {
		
	}

	public void clientInit() {
		// Sends Handshake messages to each peer before it.
		Iterator<PeerInfo> itr = list.iterator();
		while(itr.hasNext()){
			PeerInfo peer = itr.next();
			int peerId1 = Integer.parseInt(peer.getPeerId());
			if( peerId1 == peerId) continue;
			else{
				String hostName = peer.getHostname();
				int port = peer.getPort();
				// Sends handshake message to peer
				HandshakeMessage message = new HandshakeMessage(peerId1);
				Client.sendMessage(message, hostName, port);
			}
		}
	}
	
	
	
}
