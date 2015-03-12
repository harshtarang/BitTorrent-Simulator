package edu.ufl.cise.client;

import java.util.ArrayList;

import edu.ufl.cise.config.PeerInfo;

public class Peer {

	int peerId;
	ArrayList<PeerInfo> list ;
	
	public Peer( int peerId, ArrayList<PeerInfo> list ){
		this.peerId = peerId;
		this.list = list;
	}

	public int getPeerId() {
		return peerId;
	}

	public void setPeerId(int peerId) {
		this.peerId = peerId;
	}

	public ArrayList<PeerInfo> getList() {
		return list;
	}

	public void setList(ArrayList<PeerInfo> list) {
		this.list = list;
	}
	
	
	
}
