package edu.ufl.cise.client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Random;

import edu.ufl.cise.config.MetaInfo;
import edu.ufl.cise.config.PeerInfo;
import edu.ufl.cise.protocol.BitField;
import edu.ufl.cise.protocol.Choke;
import edu.ufl.cise.protocol.Interested;
import edu.ufl.cise.protocol.NotInterested;
import edu.ufl.cise.protocol.Piece;
import edu.ufl.cise.protocol.Request;
import edu.ufl.cise.protocol.SendMessage;
import edu.ufl.cise.protocol.Unchoke;
import edu.ufl.cise.server.Server;
import edu.ufl.cise.util.ExecutorPool;

public class Peer {

	private static volatile Peer instance;
	int numPeersCompleted;
	int numPiecesCompleted;
	private BitSet pieceInfo;
	private LinkedHashMap<Integer, PeerInfo> map;

	private HashMap<Integer, Boolean> piecesCurrentlyDownloading; // If the piece is currently been requested

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

	/**
	 * Initializes all the maps
	 * @param peerId
	 * @param peerMap
	 */
	public void init(int peerId, LinkedHashMap<Integer, PeerInfo> peerMap) {
		this.map = peerMap;
		this.numPeersCompleted = MetaInfo.getPeersComplete();
		int nPieces = MetaInfo.getnPieces();
		// Initialize pieceInfo
		pieceInfo = new BitSet(nPieces);
		numPiecesCompleted = 0;
		if(MetaInfo.isCompletefile()){
			pieceInfo.flip(0, nPieces);
			numPiecesCompleted = nPieces;
		}
		// Initialize currently downloading
		piecesCurrentlyDownloading = new HashMap<Integer, Boolean>();
		for( int i=0; i<nPieces; i++){
			piecesCurrentlyDownloading.put(i, false);
		}
		// Initialize all the maps
		unchokedMap         = new HashMap<Integer, Boolean>();
		unchokedMeMap       = new HashMap<Integer, Boolean>();
		interestedSent      = new HashMap<Integer, Boolean>();
		currentlyInterested = new HashMap<Integer, Boolean>();
		preferredNeighbors  = new HashMap<Integer, Boolean>();
		ArrayList<Integer> peerList = MetaInfo.getPeerList();
		Iterator<Integer> itr = peerList.iterator();
		while(itr.hasNext()){
			int peer = itr.next();
			unchokedMap.put(peer, false);
			unchokedMeMap.put(peer, false);
			interestedSent.put(peer, false);
			currentlyInterested.put(peer, false);
			preferredNeighbors.put(peer, false);
		}
		currentOptimisticUnchoked = -1;
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
		int peerId = MetaInfo.getPeerId();
		while (itr.hasNext()) {
			int peerId1 = itr.next();
			PeerInfo peerInfo = map.get(peerId1);
			if (peerId1 >= peerId) {
				continue;
			} else {
				String hostName = peerInfo.getHostname();
				int port = peerInfo.getPort();
				ClientWorker worker = new ClientWorker(peerId1, port, hostName);
				new Thread(worker).start();
			}
		}
	}
	
	/**
	 * Update preferred neighbors randomly
	 */
	public void randomSelect() {
		int k = MetaInfo.getNumPreferredNeighbours();
		ArrayList<Integer> peerList = new ArrayList<Integer>();
		HashMap<Integer, Boolean> currentlyInterested = getCurrentlyInterested();
		// Get the currently Interested neighbors
		Iterator<Integer> itr1 = currentlyInterested.keySet().iterator();
		while(itr1.hasNext()){
			int peerId = itr1.next();
			if(currentlyInterested.get(peerId)) peerList.add(peerId);
		}
		int currentInterestedSize = currentlyInterested.size();
		Random random = new Random();
		int count = 0;
		HashMap<Integer, Boolean> newlySelectedNeighbor = new HashMap<Integer, Boolean>();
		if (currentInterestedSize > k) {
			while (count < k) {
				Integer randNum = random.nextInt(k);
				int peerId = peerList.get(randNum);
				if (!newlySelectedNeighbor.containsKey(peerId)) {
					newlySelectedNeighbor.put(peerId, true);
					count++;
				}
			}
		}
		else{ // Add everyone
			for(int peerId: peerList) newlySelectedNeighbor.put(peerId, true);
		}
		// Iterate the current map and send choke and unchoke messages based on newly selected map.
		HashMap<Integer, Boolean> oldMap = getPreferredNeighbors();
		Iterator<Integer> itr = oldMap.keySet().iterator();
		while(itr.hasNext()){
			int peerID = itr.next();
			if ( oldMap.get(peerID) && newlySelectedNeighbor.containsKey(peerID)){ // if an already preferred neighbor is selected again
				continue;                                                          // keep it and enjoy. No need to send unchoke message
			}
			else if( oldMap.get(peerID) && !newlySelectedNeighbor.containsKey(peerID)){
				// Peer was a preferred neighbor but not selected. Send choke message
				Choke choke = new Choke();
				SendMessage sendMessage = new SendMessage(peerID, choke.getBytes());
				ExecutorPool.getInstance().getPool().execute(sendMessage);
				// Add the current peer entry as not interested
				newlySelectedNeighbor.put(peerID, false);
			}
			else if( !oldMap.get(peerID) && newlySelectedNeighbor.containsKey(peerID)){
				// Peer was not a preferred neighbor but now selected. Send unchoke message
				Unchoke unchoke = new Unchoke();
				SendMessage sendMessage = new SendMessage(peerID, unchoke.getBytes());
				ExecutorPool.getInstance().getPool().execute(sendMessage);
			}else{ // neither it was a preferred neighbor nor it got selected
				// Just add an entry in the current map
				newlySelectedNeighbor.put(peerID, false);
			}
		}
		// Finally update the current preferred neighbor map in Peer
		setPreferredNeighbors(newlySelectedNeighbor);
	}

	/**
	 * From the peer who have unchoked current peer, select a random piece,
	 * which is not present with current peer and has not been already requested
	 * 
	 * @return pieceID
	 */
	public int getRandomPieceToRequest(int requestFromPeerId) {
		int pieceId = -1;
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
			ArrayList<Integer> arr = new ArrayList<Integer>();
			for (pieceId = piecesInterested.nextSetBit(0); pieceId >= 0; pieceId = piecesInterested
					.nextSetBit(pieceId + 1)) {
				// check if this piece is not already currently downloading
				// add the piece to arraylist to get a random piece
				if (!piecesCurrentlyDownloading.containsKey(pieceId)) {
					arr.add(pieceId);
				}
			}
			// Generate a random number if there exists a piece
			if(!arr.isEmpty()){
				Random random = new Random();
				int index = random.nextInt(arr.size());
				pieceId = arr.get(index);
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
				&& (MetaInfo.getNumPeers() == numPeersCompleted)) {
			shutdown();
			return true;
		}
		return false;
	}

	private void shutdown() {
		// TODO Auto-generated method stub
	}

	/**
	 * Check among the peers I have sent interested message previously whom I am
	 * not longer interested in.
	 */
	public void determineAndSendInterstedMessageToPeers() {
		Iterator<Integer> itr = interestedSent.keySet().iterator();
		while (itr.hasNext()) {
			int peerId = itr.next();
			determineAndSendInterestedMessage(peerId);
		}
	}

	/**
	 * Compute bitset difference between current peer and the given peer Based
	 * on difference and current interested state send I/NI message.
	 * 
	 * @param peerId2
	 * @return
	 */
	public void determineAndSendInterestedMessage(int peerId2) {
		PeerInfo peerInfo = map.get(peerId2); // peer info of the given peer
		BitSet currentPeerBitSet = (BitSet) pieceInfo.clone();
		// Flip current bit set to get pieces intersted
		currentPeerBitSet.flip(0, currentPeerBitSet.length());
		// Do an and to get intersection
		currentPeerBitSet.and(peerInfo.getPiecesInterested());
		if (interestedSent.containsKey(peerId2) && interestedSent.get(peerId2)) {
			if (currentPeerBitSet.isEmpty()) { // if current peer is not
												// interested in peerID2
				// Send not interested message
				NotInterested message = new NotInterested();
				SendMessage sendMessage = new SendMessage(peerId2,
						message.getBytes());
				ExecutorPool.getInstance().getPool().execute(sendMessage);

				// TODO: update interestedSent map
				interestedSent.put(peerId2, false);
			} else {
				// Do nothing
			}
		} else { // current peer is not interested in peerId2 currently
			if (currentPeerBitSet.isEmpty()) { // if current peer is not
												// interested in peerID2 still
				// Don't do anything
			} else { // now current peer is interested
						// Send interested message
				Interested message = new Interested();
				SendMessage sendMessage = new SendMessage(peerId2,
						message.getBytes());
				ExecutorPool.getInstance().getPool().execute(sendMessage);

				// TODO: update interestedSent map
				interestedSent.put(peerId2, true);
			}
		}
	}

	public void updateBitSets(int peerId2, BitSet bs) {
		PeerInfo peerInfo = map.get(MetaInfo.getPeerId());
		peerInfo.setPieceInfo(bs);
		BitSet currentPieceInfo = (BitSet) pieceInfo.clone();
		// Flip the bits to get pieces interested in
		currentPieceInfo.flip(0, currentPieceInfo.length());
		// Take and AND with peer's bitset
		currentPieceInfo.and(bs);
		// update the map
		peerInfo.setPiecesInterested(currentPieceInfo);
	}
	
	public void updateOwnBitSet(int pieceId) {
		piecesCurrentlyDownloading.put(pieceId, false);
		numPiecesCompleted--;
		pieceInfo.set(pieceId);
		if(numPiecesCompleted == 0){
			numPeersCompleted++;
		}
	}
	
	public void updatePeerBitset(int peerId, int pieceId) {
		PeerInfo info = map.get(peerId);
		info.getPieceInfo().set(pieceId);
		info.updatePieceInterested(); // decrement pieces interested in
		if(info.getNumPiecesInterested() == 0){
			numPeersCompleted++;
		}
	}

	// Send message related methods
	
	/**
	 * update flag and send bitfield message
	 * @param peerId
	 */
	public void updateAndSendBitField(int peerId) {
		BitField bitFieldMessage = getBitFieldMessage();
		SendMessage message = new SendMessage(peerId, bitFieldMessage.getBytes());
		ExecutorPool.getInstance().getPool().execute(message);
		updateBitFieldSent(peerId);
	}

	/**
	 * Update and sent handshake message
	 * @param peerId
	 * @param message
	 */
	public void updateAndSendHandshakeMessage(int peerId, SendMessage message){
		ExecutorPool.getInstance().getPool().execute(message);
		updateHandshakeSent(peerId);
	}
	
	public void updateAndSendChoke(int peerId) {
		if (getUnchokedMap().get(peerId)) { // returns true if unchoked
			// Send choke and update map
			getUnchokedMap().put(peerId, false);
			Choke choke = new Choke();
			SendMessage sendMessage = new SendMessage(peerId, choke.getBytes());
			ExecutorPool.getInstance().getPool().execute(sendMessage);
		}

	}

	public void determineAndSendPieceRequest(int peerId2) {
		if (unchokedMeMap.get(peerId2)) {
			int pieceId = getRandomPieceToRequest(peerId2);
			sendRequestMessage(peerId2, pieceId);
		}
	}

	/**
	 * Check if it is still unchoked.
	 * Sends a request message to the peer for the pieceId
	 * Updates the piecesCurrentlyDownloading map
	 * @param peerId2
	 * @param pieceId
	 */
	public void sendRequestMessage(int peerId2, int pieceId) {
		if(!unchokedMeMap.get(peerId2)) return;
		Request requestMessage = new Request(pieceId);
		SendMessage sendMessage = new SendMessage(peerId2,
				requestMessage.getBytes());
		ExecutorPool.getInstance().getPool().execute(sendMessage);
		piecesCurrentlyDownloading.put(pieceId, true);
	}

	/**
	 * Check if the peer is still unchoked.
	 * Send the piece message
	 * @param peerId
	 * @param pieceMessage
	 */
	public void sendPieceMessage(int peerId, Piece pieceMessage){
		if(!unchokedMap.get(peerId)) return;
		SendMessage sendMessage = new SendMessage(peerId,
				pieceMessage.getBytes());
		ExecutorPool.getInstance().getPool().execute(sendMessage);
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

	// PeerInfo related methods
	
	public boolean isHandshakeSent(int peerId) {
		PeerInfo peerInfo = map.get(peerId);
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

	public void updateBitFieldSent(int peerId) {
		PeerInfo peerInfo = map.get(peerId);
		peerInfo.setBitFieldSent(true);
	}
	
	// Getters and Setters
	
	public LinkedHashMap<Integer, PeerInfo> getMap() {
		return map;
	}

	public void setMap(LinkedHashMap<Integer, PeerInfo> map) {
		this.map = map;
	}

	public int getCurrentOptimisticUnchoked() {
		return currentOptimisticUnchoked;
	}

	public void setCurrentOptimisticUnchoked(int currentOptimisticUnchoked) {
		this.currentOptimisticUnchoked = currentOptimisticUnchoked;
	}

	public int getNumPeersCompleted() {
		return numPeersCompleted;
	}

	public void setNumPeersCompleted(int numPeersCompleted) {
		this.numPeersCompleted = numPeersCompleted;
	}

	public HashMap<Integer, Boolean> getPiecesCurrentlyDownloading() {
		return piecesCurrentlyDownloading;
	}

	public void setPiecesCurrentlyDownloading(
			HashMap<Integer, Boolean> piecesCurrentlyDownloading) {
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

}
