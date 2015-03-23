package edu.ufl.cise.config;

import java.net.Socket;
import java.util.BitSet;

public class PeerInfo {

	String peerId;
	String hostname;
	int port;
	boolean isCompleteFile;
	boolean isChoke;
	boolean isChokedBy;
	boolean isOptimisticUnchoked;
	boolean interested;
	float downloadSpeed;
	Socket socket;
	BitSet pieceInfo;
	
	public boolean isChoke() {
		return isChoke;
	}

	public void setChoke(boolean isChoke) {
		this.isChoke = isChoke;
	}

	public boolean isChokedBy() {
		return isChokedBy;
	}

	public void setChokedBy(boolean isChokedBy) {
		this.isChokedBy = isChokedBy;
	}

	public boolean isOptimisticUnchoked() {
		return isOptimisticUnchoked;
	}

	public void setOptimisticUnchoked(boolean isOptimisticUnchoked) {
		this.isOptimisticUnchoked = isOptimisticUnchoked;
	}

	public boolean isInterested() {
		return interested;
	}

	public void setInterested(boolean interested) {
		this.interested = interested;
	}

	public float getDownloadSpeed() {
		return downloadSpeed;
	}

	public void setDownloadSpeed(float downloadSpeed) {
		this.downloadSpeed = downloadSpeed;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public PeerInfo(){}
	
	public PeerInfo( String peerId,
			String hostname,
			int port,
			boolean isCompleteFile){
		this.peerId = peerId;
		this.hostname = hostname;
		this.port = port;
		this.isCompleteFile = isCompleteFile;
	}

	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}
	
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public boolean isCompleteFile() {
		return isCompleteFile;
	}
	public void setCompleteFile(boolean isCompleteFile) {
		this.isCompleteFile = isCompleteFile;
	}
	
}
