package edu.ufl.cise.config;

public class PeerInfo {

	String peerId;
	String hostname;
	int port;
	boolean isCompleteFile;
	
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
