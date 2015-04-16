package edu.ufl.cise.client;

import java.io.IOException;
import java.io.OutputStream;
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
import edu.ufl.cise.util.FileHandlingUtils;
import edu.ufl.cise.util.Logger;

public class Peer {

	private static volatile Peer instance;
	int numPeersCompleted;
	int numPiecesCompleted;
	private BitSet pieceInfo;
	private LinkedHashMap<Integer, PeerInfo> map;

	private HashMap<Integer, Boolean> piecesCurrentlyDownloading; // If the
																	// piece is
																	// currently
																	// been
																	// requested

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
	private int currentOptimisticUnchoked;
	private int count; // For testing purposes

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
	 * 
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
		if (MetaInfo.isCompletefile()) {
			pieceInfo.flip(0, nPieces);
			numPiecesCompleted = nPieces;
		}
		// Initialize currently downloading
		piecesCurrentlyDownloading = new HashMap<Integer, Boolean>();
		for (int i = 0; i < nPieces; i++) {
			piecesCurrentlyDownloading.put(i, false);
		}
		// Initialize all the maps
		unchokedMap = new HashMap<Integer, Boolean>();
		unchokedMeMap = new HashMap<Integer, Boolean>();
		interestedSent = new HashMap<Integer, Boolean>();
		currentlyInterested = new HashMap<Integer, Boolean>();
		preferredNeighbors = new HashMap<Integer, Boolean>();
		ArrayList<Integer> peerList = MetaInfo.getPeerList();
		Iterator<Integer> itr = peerList.iterator();
		while (itr.hasNext()) {
			int peer = itr.next();
			unchokedMap.put(peer, false);
			unchokedMeMap.put(peer, false);
			interestedSent.put(peer, false);
			currentlyInterested.put(peer, false);
			preferredNeighbors.put(peer, false);
		}
		currentOptimisticUnchoked = -1;
		// For testing purposes
		count = peerId;
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

	public void randomSelectOptimisticUnchoke() {
		Random random = new Random();
		ArrayList<Integer> interestedPeerList = new ArrayList<Integer>();
		HashMap<Integer, Boolean> currentlyInterested = getCurrentlyInterested();
		HashMap<Integer, Boolean> currentPreferredNeigbor = getPreferredNeighbors();
		int selectedOUN = -1;
		int count = 0;

		// Get the currently Interested neighbors
		Iterator<Integer> itr1 = currentlyInterested.keySet().iterator();
		while (itr1.hasNext()) {
			int peerId = itr1.next();
			if (currentlyInterested.get(peerId)
					&& peerId != MetaInfo.getPeerId())
				interestedPeerList.add(peerId);
		}
		int currentInterestedSize = interestedPeerList.size();

		if (currentInterestedSize > 0) {
			while (count++ < currentInterestedSize) { // to break the possibly
														// infinite loop when
														// interested ones are
														// same as preferred
														// neighbors
				Integer randNum = random.nextInt(currentInterestedSize);
				int peerId = interestedPeerList.get(randNum);
				if (currentInterestedSize == 1) { // if only one is
													// interested then
													// just take it
					selectedOUN = peerId;
					break;
				} else if (currentPreferredNeigbor.get(peerId)) { // more than 1
																	// interested
																	// but
																	// exists in
																	// preferred
																	// neighbor
					selectedOUN = peerId;
					continue;
				}
			}
		} else { // if no one is interested just return
			return;
		}

		// Log the newly selected OUN
		String logMessage = "Peer " + MetaInfo.getPeerId() + " has the optimistically unchoked neighbor " + 
						selectedOUN;
		Logger.getInstance().log(logMessage);
		
		// Send choke or unchoke message
		int previouseOUN = currentOptimisticUnchoked;
		if (previouseOUN != selectedOUN) {
			// Send choke message to previous OUN if its not in current selected
			// neighbor

			if (previouseOUN != -1) {
				if (!preferredNeighbors.get(previouseOUN)) {
					Choke choke = new Choke();
					SendMessage sendMessage = new SendMessage(previouseOUN,
							choke.getBytes());
					ExecutorPool.getInstance().getPool().execute(sendMessage);
				}
			}
			// Set the current OUN
			setCurrentOptimisticUnchoked(selectedOUN);
			// Send the unchoke message if its not already unchoked
			if (!preferredNeighbors.get(selectedOUN)) {
				Unchoke unchoke = new Unchoke();
				SendMessage sendMessage = new SendMessage(selectedOUN,
						unchoke.getBytes());
				ExecutorPool.getInstance().getPool().execute(sendMessage);
			}
		}
	}

	/**
	 * Update preferred neighbors randomly
	 */
	public void randomSelect() {
		Random random = new Random();
		HashMap<Integer, Boolean> newlySelectedNeighbor = new HashMap<Integer, Boolean>();
		ArrayList<Integer> interestedPeerList = new ArrayList<Integer>();
		HashMap<Integer, Boolean> currentlyInterested = getCurrentlyInterested();
		int k = MetaInfo.getNumPreferredNeighbours();
		int count = 0;
		StringBuilder sb = new StringBuilder();

		// Get the currently Interested neighbors in a ArrayList
		Iterator<Integer> itr1 = currentlyInterested.keySet().iterator();
		while (itr1.hasNext()) {
			int peerId = itr1.next();
			if (currentlyInterested.get(peerId)
					&& peerId != MetaInfo.getPeerId())
				interestedPeerList.add(peerId);
		}
		int currentInterestedSize = interestedPeerList.size();
		int safeCount = 0; // to break the highly unlikely possibility of an
							// infinite loop.
		if (currentInterestedSize > k) {
			while (count < k && safeCount < 3 * k) {
				safeCount++;
				Integer randNum = random.nextInt(k);
				int peerId = interestedPeerList.get(randNum);
				if (peerId == MetaInfo.getPeerId()) {
					System.out
							.println("********************************************* this is wrong************");
					continue; // should never reach here
				}
				if (!newlySelectedNeighbor.containsKey(peerId)) {
					newlySelectedNeighbor.put(peerId, true);
					count++;
				}
			}
		} else if (currentInterestedSize <= k && currentInterestedSize != 0) { // Add
																				// everyone
			for (int peerId : interestedPeerList) {
				newlySelectedNeighbor.put(peerId, true);
				sb.append(peerId);
				sb.append(",");
			}
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1); // to remove the last comma
		}

		// Log the newlySelected neighbor
		String logMessage = "Peer " + MetaInfo.getPeerId()
				+ " has the preferred neighbors: " + sb.toString();
		Logger.getInstance().log(logMessage);

		// Iterate the current map and send choke and unchoke messages based on
		// newly selected map.
		HashMap<Integer, Boolean> oldMap = getPreferredNeighbors();
		Iterator<Integer> itr = oldMap.keySet().iterator();
		while (itr.hasNext()) {
			int peerID = itr.next();
			if (oldMap.get(peerID) && newlySelectedNeighbor.containsKey(peerID)) { // if
																					// an
																					// already
																					// preferred
																					// neighbor
																					// is
																					// selected
																					// again
				continue; // keep it and enjoy. No need to send unchoke message
			} else if (oldMap.get(peerID)
					&& !newlySelectedNeighbor.containsKey(peerID)) {
				// Peer was a preferred neighbor but not selected. Send choke
				// message
				getUnchokedMap().put(peerID, false);
				Choke choke = new Choke();
				SendMessage sendMessage = new SendMessage(peerID,
						choke.getBytes());
				ExecutorPool.getInstance().getPool().execute(sendMessage);
				// Add the current peer entry as not a neighbor
				newlySelectedNeighbor.put(peerID, false);
			} else if (!oldMap.get(peerID)
					&& newlySelectedNeighbor.containsKey(peerID)) {
				// Peer was not a preferred neighbor but now selected. Send
				// unchoke message
				getUnchokedMap().put(peerID, true);
				Unchoke unchoke = new Unchoke();
				SendMessage sendMessage = new SendMessage(peerID,
						unchoke.getBytes());
				ExecutorPool.getInstance().getPool().execute(sendMessage);
			} else { // neither it was a preferred neighbor nor it got selected
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
		piecesInterested.flip(0, MetaInfo.getnPieces()); // and flip it
															// completely to
															// make it pieces
															// interested
		PeerInfo peerInfo = map.get(requestFromPeerId);
		int n = MetaInfo.getnPieces();
		piecesInterested.and(peerInfo.getPieceInfo());
		if (!piecesInterested.isEmpty()) { // if there is some piece which can
											// be requested
			ArrayList<Integer> arr = new ArrayList<Integer>();
			for (pieceId = piecesInterested.nextSetBit(0); pieceId >= 0; pieceId = piecesInterested
					.nextSetBit(pieceId + 1)) {
				// check if this piece is not already currently downloading
				// add the piece to arraylist to get a random piece
				if (!piecesCurrentlyDownloading.get(pieceId)) {
					arr.add(pieceId);
				}
			}
			// Generate a random number if there exists a piece
			if (!arr.isEmpty()) {
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
		if (MetaInfo.getnPieces() == numPiecesCompleted) {
			String logMessage = "Peer " + MetaInfo.getPeerId()
					+ " has downloaded the complete file ";
			Logger.getInstance().log(logMessage);

			FileHandlingUtils fh = new FileHandlingUtils();
			fh.finish();
		}

		if ((MetaInfo.getnPieces() == numPiecesCompleted)
				&& (MetaInfo.getNumPeers() == numPeersCompleted)) {
			// Assemble all the pieces
			FileHandlingUtils fh = new FileHandlingUtils();
			fh.finish();
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

			if (peerId != MetaInfo.getPeerId() && interestedSent.get(peerId)) {

				determineAndSendInterestedMessage(peerId);
			}
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
		currentPeerBitSet.flip(0, MetaInfo.getnPieces());
		// Do an and to get intersection
		currentPeerBitSet.and(peerInfo.getPieceInfo());
		if (interestedSent.containsKey(peerId2) && interestedSent.get(peerId2)) {
			if (currentPeerBitSet.isEmpty()) { // if current peer is not
												// interested in peerID2
				// Send not interested message

				NotInterested message = new NotInterested();
				SendMessage sendMessage = new SendMessage(peerId2,
						message.getBytes());
				ExecutorPool.getInstance().getPool().execute(sendMessage);

				interestedSent.put(peerId2, false);
			} else {
				// Do nothing
			}
		} else { // current peer is not interested in peerId2 currently
			if (currentPeerBitSet.isEmpty()) { // if current peer is not
												// interested in peerID2 still
				// Don't do anything

				NotInterested message = new NotInterested();
				SendMessage sendMessage = new SendMessage(peerId2,
						message.getBytes());
				ExecutorPool.getInstance().getPool().execute(sendMessage);

			} else { // now current peer is interested
						// Send interested message
				Interested message = new Interested();
				SendMessage sendMessage = new SendMessage(peerId2,
						message.getBytes());
				ExecutorPool.getInstance().getPool().execute(sendMessage);

				interestedSent.put(peerId2, true);
			}
		}
	}

	/**
	 * Update the piece info for the corresponding peer
	 * 
	 * @param peerId2
	 * @param bs
	 */
	public void updateBitSets(int peerId2, BitSet bs) {
		PeerInfo peerInfo = map.get(peerId2);
		peerInfo.setPieceInfo(bs);
	}

	public void updateOwnBitSet(int pieceId, int peerId2) {
		piecesCurrentlyDownloading.put(pieceId, false);
		numPiecesCompleted++;

		String logMessage = "Peer " + MetaInfo.getPeerId()
				+ " has downloaded the piece " + pieceId + " from " + peerId2
				+ ". Now the number of pieces it has is " + numPiecesCompleted;
		Logger.getInstance().log(logMessage);

		pieceInfo.set(pieceId);
		if (numPiecesCompleted == MetaInfo.getnPieces()) {
			numPeersCompleted++;
		}
	}

	public void updatePeerBitset(int peerId, int pieceId) {
		PeerInfo info = map.get(peerId);
		info.getPieceInfo().set(pieceId);
		info.updatePieceInterested(); // decrement pieces interested in
		if (info.getNumPiecesInterested() == 0) {
			numPeersCompleted++;
		}
	}

	// Send message related methods

	/**
	 * update flag and send bitfield message
	 * 
	 * @param peerId
	 */
	public void updateAndSendBitField(int peerId) {
		BitField bitFieldMessage = getBitFieldMessage();
		SendMessage message = new SendMessage(peerId,
				bitFieldMessage.getBytes());
		ExecutorPool.getInstance().getPool().execute(message);
		updateBitFieldSent(peerId);
	}

	/**
	 * Update and sent handshake message
	 * 
	 * @param peerId
	 * @param message
	 */
	public void updateAndSendHandshakeMessage(int peerId, SendMessage message) {
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

	/**
	 * Check if it is still unchoked. Sends a request message to the peer for
	 * the pieceId Updates the piecesCurrentlyDownloading map
	 * 
	 * @param peerId2
	 * @param pieceId
	 */
	public void determineAndSendPieceRequest(int peerId2) {
		if (!unchokedMeMap.get(peerId2))
			return;
		int pieceId = getRandomPieceToRequest(peerId2);
		if (pieceId != -1)
			sendRequestMessage(peerId2, pieceId);
	}

	public void sendRequestMessage(int peerId2, int pieceId) {
		if (!unchokedMeMap.get(peerId2))
			return;
		Request requestMessage = new Request(pieceId);
		SendMessage sendMessage = new SendMessage(peerId2,
				requestMessage.getBytes());
		ExecutorPool.getInstance().getPool().execute(sendMessage);
		piecesCurrentlyDownloading.put(pieceId, true);
	}

	/**
	 * Check if the peer is still unchoked. Send the piece message
	 * 
	 * @param peerId
	 * @param pieceMessage
	 */
	public void sendPieceMessage(int peerId, Piece pieceMessage) {
		if (!unchokedMap.get(peerId))
			return;
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

	public void updateSocket(int peerID2, Socket clientSocket, OutputStream out) {
		PeerInfo peerInfo = map.get(peerID2);
		peerInfo.setSocket(clientSocket);
		peerInfo.setOut(out);
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

	public int getCount() {
		count++;
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
