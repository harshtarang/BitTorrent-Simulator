package edu.ufl.cise.client;

import java.io.IOException;
import java.net.Socket;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import edu.ufl.cise.config.MetaInfo;
import edu.ufl.cise.config.PeerInfo;
import edu.ufl.cise.protocol.BitField;
import edu.ufl.cise.protocol.Choke;
import edu.ufl.cise.protocol.Interested;
import edu.ufl.cise.protocol.NotInterested;
import edu.ufl.cise.protocol.Request;
import edu.ufl.cise.protocol.SendMessage;
import edu.ufl.cise.server.Server;
import edu.ufl.cise.util.ExecutorPool;

public class Peer {

	private static volatile Peer instance;
	private int peerId;
	int numPeers;
	int numPeersCompleted;
	int numPiecesCompleted;
	private BitSet pieceInfo;
	private LinkedHashMap<Integer, PeerInfo> map;

	private HashMap<Integer, Integer> piecesCurrentlyDownloading; // Maps pieces
																	// to the
																	// peer Id

	private HashMap<Integer, Boolean> unchokedMap; // Peers who I have currently
													// unchoked . true if
													// unchoked
	private HashMap<Integer, Boolean> unchokedMeMap; // Peers who have currently
														// unchoked me. true if
														// unchoked

	private HashMap<Integer, Boolean> interestedSent; // Peers to whom
														// interested is sent.
														// true if interested
														// sent
	private HashMap<Integer, Boolean> currentlyInterested; // Peers currently
															// interested in me.
															// true if
															// interested

	private HashMap<Integer, Boolean> preferredNeighbors; // Current preferred
															// neighbors
	int currentOptimisticUnchoked;

	private HashMap<String, Integer> hostNameToIdMap; // For checking which
														// peerId this
														// connection request
														// belongs to when
														// a connection request
														// comes.

	public void updatePeerBitset(int peerId, int pieceId) {
		PeerInfo info = map.get(peerId);
		info.getPieceInfo().set(pieceId);
	}

	/**
	 * Check both unchoked map and OUN field to return if a peer is unchoked
	 * 
	 * @param peerId
	 * @return
	 */
	public boolean isUnchoked(int peerId) {
		if (unchokedMap.get(peerId) || (currentOptimisticUnchoked == peerId))
			return true;
		return false;
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

	public void Serverinit() throws IOException {
		int portNumber = MetaInfo.getPortNumber();
		Server server = new Server(portNumber);
		new Thread(server).start();
	}

	/**
	 * Sends Handshake messages to each peer before it.
	 */
	public void clientInit() {
		Iterator<Integer> itr = map.keySet().iterator();
		while (itr.hasNext()) {
			int peerId1 = itr.next();
			PeerInfo peerInfo = map.get(peerId1);
			if (peerId1 >= peerId){
				continue;
			} else {
				String hostName = peerInfo.getHostname();
				int port = peerInfo.getPort();
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

	public void setCurrentlyInterested(
			HashMap<Integer, Boolean> currentlyInterested) {
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

	public void setPreferredNeighbors(
			HashMap<Integer, Boolean> preferredNeighbors) {
		this.preferredNeighbors = preferredNeighbors;
	}

	public HashMap<String, Integer> getHostNameToIdMap() {
		return hostNameToIdMap;
	}

	public void setHostNameToIdMap(HashMap<String, Integer> hostNameToIdMap) {
		this.hostNameToIdMap = hostNameToIdMap;
	}

	public BitSet getPieceInfo() {
		return pieceInfo;
	}

	public void setPieceInfo(BitSet pieceInfo) {
		this.pieceInfo = pieceInfo;
	}

	public HashMap<Integer, Boolean> getUnchokedMeMap() {
		return unchokedMeMap;
	}

	public void setUnchokedMeMap(HashMap<Integer, Boolean> unchokedMeMap) {
		this.unchokedMeMap = unchokedMeMap;
	}

	public int getNumPiecesCompleted() {
		return numPiecesCompleted;
	}

	public void setNumPiecesCompleted(int numPiecesCompleted) {
		this.numPiecesCompleted = numPiecesCompleted;
	}

	/**
	 * From the peers who have unchoked current peer, select a random piece,
	 * which is not present with current peer and has not been already requested
	 * 
	 * @return pieceID
	 */
	public int getRandomPieceToRequest(int requestFromPeerId) {
		int pieceId = -1;
	//	Iterator<Integer> itr = unchokedMeMap.keySet().iterator();
		BitSet piecesInterested = (BitSet) pieceInfo.clone(); // clone the
																// existing
																// pieceInfo
		piecesInterested.flip(0, piecesInterested.size()); // and flip it
															// completely to
															// make it pieces
															// interested
		PeerInfo peerInfo = map.get(requestFromPeerId);
		piecesInterested.and(peerInfo.getPieceInfo());
		if (!piecesInterested.isEmpty()) { // if there is some piece which can
											// be requested
			for (pieceId = piecesInterested.nextSetBit(0); pieceId >= 0; pieceId = piecesInterested
					.nextSetBit(pieceId + 1)) {
				// check if this piece is not already currently downloading
				// return
				if (!piecesCurrentlyDownloading.containsKey(pieceId)) {
					// update the map
					piecesCurrentlyDownloading.put(pieceId, requestFromPeerId);
					return pieceId;
				}
			}
		}
		return pieceId;
	}

	/**
	 * Evaluate if system needs to be shutdown. If yes take necessary actions
	 * which I currently do not have any idea about.
	 * 
	 * @return
	 */
	public boolean evaluateSystemShutDown() {
		// Check if the current peer has all the pieces
		// and all the peers have completed
		if ((MetaInfo.getnPieces() == numPiecesCompleted)
				|| (MetaInfo.getNumPeers() == numPeersCompleted)) {
			shutdown();
			return true;
		}
		return false;
	}

	private void shutdown() {
		// TODO Auto-generated method stub
	}

	/**
	 * Check among the peers I have sent interested message previously
	 * whom I am not longer interested in. 
	 */
	public void determineToSendInterstedMessageToPeers() {
		Iterator<Integer> itr = interestedSent.keySet().iterator();
		while(itr.hasNext()){
			int peerId = itr.next();
			determineToSendInterestedMessage(peerId);
		}
	}

	/**
	 * Compute bitset difference between current peer and the given peer Based
	 * on difference and current interested state send I/NI message.
	 * 
	 * @param peerId2
	 * @return
	 */
	public void determineToSendInterestedMessage(int peerId2) {
		PeerInfo peerInfo = map.get(peerId2); // peer info of the given peer
		BitSet currentPeerBitSet = (BitSet) pieceInfo.clone();
		// Flip current bit set to get pieces intersted
		currentPeerBitSet.flip(0, currentPeerBitSet.length());
		// Do an and to get intersection
		currentPeerBitSet.and(peerInfo.getPiecesInterested());
		if(interestedSent.containsKey(peerId2) && interestedSent.get(peerId2)){
			if( currentPeerBitSet.isEmpty()){ // if current peer is not interested in peerID2
				// Send not interested message
				NotInterested message = new NotInterested();
				SendMessage sendMessage = new SendMessage(peerId2, message.getBytes());
				ExecutorPool.getInstance().getPool().execute(sendMessage);
				
				//TODO: update interestedSent map
				interestedSent.put(peerId2,false);
			}
			else{
				// Do nothing
			}
		}
		else{  // current peer is not interested in peerId2 currently
			if(currentPeerBitSet.isEmpty()){ // if current peer is not interested in peerID2 still
				//Don't do anything
			}
			else{ // now current peer is interested
				// Send interested message
				Interested message = new Interested();
				SendMessage sendMessage = new SendMessage(peerId2, message.getBytes());
				ExecutorPool.getInstance().getPool().execute(sendMessage);
				
				//TODO: update interestedSent map
				interestedSent.put(peerId2, true);
			}
		}
	}

	public void updateBitSets(int peerId2, BitSet bs) {
		PeerInfo peerInfo = map.get(peerId);
		peerInfo.setPieceInfo(bs);
		BitSet currentPieceInfo = (BitSet) pieceInfo.clone();
		// Flip the bits to get pieces interested in
		currentPieceInfo.flip(0, currentPieceInfo.length());
		// Take and AND with peer's bitset
		currentPieceInfo.and(bs);
		// update the map
		peerInfo.setPiecesInterested(currentPieceInfo);
	}

	public boolean isHandshakeSent(int peerId) {
		PeerInfo peerInfo  = map.get(peerId);
		return peerInfo.isHandShakeSent();
	}

	public BitField getBitFieldMessage() {
		BitField message = new BitField(pieceInfo.toByteArray());
		return message;
	}

	public void updateSocket(int peerID2, Socket clientSocket) {
		PeerInfo peerInfo = map.get(peerID2);
		peerInfo.setSocket(clientSocket);
	}

	public void updateHandshakeSent(int peerId) {
		PeerInfo peerInfo = map.get(peerId);
		peerInfo.setHandShakeSent(true);
	}
	
	public void updateAndSendChoke(int peerId){
		if(getUnchokedMap().get(peerId)){ // returns true if unchoked
			// Send choke and update map
			getUnchokedMap().put(peerId, false);
			Choke choke = new Choke();
			SendMessage sendMessage = new SendMessage(peerId, choke.getBytes());
			ExecutorPool.getInstance().getPool().execute(sendMessage);
		}

	}

	public void determineAndSendPieceRequest(int peerId2) 
	{
		if(unchokedMeMap.get(peerId2))
		{
		
			int pieceId=getRandomPieceToRequest(peerId2);
		
			
			sendRequestMessage(peerId2,pieceId);
			

		}
	}

	private void sendRequestMessage(int peerId2, int pieceId) {
		// TODO Auto-generated method stub
		Request requestMessage = new Request(pieceId);
		
		SendMessage sendMessage = new SendMessage(peerId,
				requestMessage.getBytes());
			ExecutorPool.getInstance().getPool().execute(sendMessage);
			
			piecesCurrentlyDownloading.put(pieceId,peerId2);
	}

}
