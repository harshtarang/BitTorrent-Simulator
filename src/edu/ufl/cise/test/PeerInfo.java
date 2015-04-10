package edu.ufl.cise.test;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedHashMap;


public class PeerInfo {

	private static volatile PeerInfo instance;

	private int peerId;
	private int portNumber;
	private String hostName;
	private LinkedHashMap<Integer, State> map;
	
	public int getPeerId() {
		return peerId;
	}

	public void setPeerId(int peerId) {
		this.peerId = peerId;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public LinkedHashMap<Integer, State> getMap() {
		return map;
	}

	public void setMap(LinkedHashMap<Integer, State> map) {
		this.map = map;
	}

	public void updateSocket(int peerId, Socket socket){
		State state = map.get(peerId);
		state.setSocket(socket);
	}
	
	public void updateFirstMessageSent(int peerId){
		//System.out.println("Updating first message sent");
		State state = map.get(peerId);
		state.setFirstMessageSent(true);
	}

	public void updateFirstMessageReceived(int peerId){
		//System.out.println("Updating first message received");
		State state = map.get(peerId);
		state.setFirstMessageReceived(true);
	}

	public void updateSecondMessageReceived(int peerId){
		//System.out.println("Updating second message received");
		State state = map.get(peerId);
		state.setSecondMessageReceived(true);
	}

	public void updateSecondMessageSent(int peerId){
		//System.out.println("Updating second message sent");
		State state = map.get(peerId);
		state.setSecondMessageSent(true);
	}
	
	
	public static PeerInfo getInstance() {
		if (instance == null) {
			synchronized (PeerInfo.class) {
				if (instance == null)
					instance = new PeerInfo();
			}
		}
		return instance;
	}

	private PeerInfo() {
	}

	public void init(int peerId, LinkedHashMap<Integer, State> peerMap) {
		this.peerId = peerId;
		this.map = peerMap;
	}

	public void Serverinit() throws IOException {
		System.out.println("Starting server: " + peerId);
		Server server = new Server(portNumber);
		new Thread(server).start();
	}

	/**
	 * Sends Handshake messages to each peer before it.
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public void clientInit() throws UnknownHostException, IOException {
		System.out.println("Starting client: " + peerId);
		Iterator<Integer> itr = map.keySet().iterator();
		while (itr.hasNext()) {
			Integer peerId1 = itr.next();
			State peerInfo = map.get(peerId1);
			if (peerId1 >= peerId){
				continue;
			} else {
				System.out.println("Starting client worker for: " + peerId1);
				int port = peerInfo.getPort();
				ClientWorker worker = new ClientWorker(peerId1, port);
				new Thread(worker).start();
			}
		}
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
}
