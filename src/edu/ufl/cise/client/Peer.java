package edu.ufl.cise.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

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
	private volatile int numPeersCompleted;
	private volatile int numPiecesCompleted;

	private BitSet pieceInfo;
	private LinkedHashMap<Integer, PeerInfo> map;

	private HashMap<Integer, Boolean> isConnected;
	private HashMap<Integer, Integer> piecesCurrentlyDownloading; // If the
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

	private HashMap<Integer, Integer> piecesDownloadedFrom;

	private int currentOptimisticUnchoked;
	private int count; // For testing purposes

	public synchronized static Peer getInstance() {
		if (instance == null) {
			instance = new Peer();
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
			this.numPeersCompleted = 1;
		}
		// Initialize currently downloading
		piecesCurrentlyDownloading = new HashMap<Integer, Integer>();
		for (int i = 0; i < nPieces; i++) {
			piecesCurrentlyDownloading.put(i, -1);
		}
		// Initialize all the maps
		unchokedMap = new HashMap<Integer, Boolean>();
		unchokedMeMap = new HashMap<Integer, Boolean>();
		interestedSent = new HashMap<Integer, Boolean>();
		currentlyInterested = new HashMap<Integer, Boolean>();
		preferredNeighbors = new HashMap<Integer, Boolean>();
		isConnected = new HashMap<Integer, Boolean>();
		piecesDownloadedFrom = new HashMap<Integer, Integer>();

		ArrayList<Integer> peerList = MetaInfo.getPeerList();
		Iterator<Integer> itr = peerList.iterator();
		while (itr.hasNext()) {
			int peer = itr.next();
			unchokedMap.put(peer, false);
			unchokedMeMap.put(peer, false);
			interestedSent.put(peer, false);
			currentlyInterested.put(peer, false);
			preferredNeighbors.put(peer, false);
			isConnected.put(peer, false);
			piecesDownloadedFrom.put(peer, 0);
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
				try
				{
					Thread.sleep(5000);
				}
				catch(InterruptedException e)
				{
					
				}
				String hostName = peerInfo.getHostname();
				int port = peerInfo.getPort();
				ClientWorker worker = new ClientWorker(peerId1, port, hostName);
				//System.out.println("Connecting to: " + peerId1);
				new Thread(worker).start();

				try
				{
					Thread.sleep(1000);
				}
				catch(InterruptedException e)
				{
					
				}
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
					&& peerId != MetaInfo.getPeerId()
					&& !currentPreferredNeigbor.get(peerId))
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
				selectedOUN = peerId;
			}
		} else { // if no one is interested just return
			// return;
		}

		// Log the newly selected OUN
		String logMessage = "Peer " + MetaInfo.getPeerId()
				+ " has the optimistically unchoked neighbor " + selectedOUN;
		//Logger.getInstance().log(logMessage);
		Logger.log(logMessage);

		if (selectedOUN == -1) {
			setCurrentOptimisticUnchoked(selectedOUN);
			return;
		}

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
					unchokedMap.put(previouseOUN, false);
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
				unchokedMap.put(selectedOUN, true);

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
				Integer randNum = random.nextInt(currentInterestedSize);
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
			}
		}

		for (int peerId : newlySelectedNeighbor.keySet()) {
			sb.append(peerId);
			sb.append(",");
		}

		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1); // to remove the last comma
		}

		// Log the newlySelected neighbor
		String logMessage = "Peer " + MetaInfo.getPeerId()
				+ " has the preferred neighbors: " + sb.toString();
		//Logger.getInstance().log(logMessage);
		Logger.log(logMessage);


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

		//String message = "Pieces interested in: " + piecesInterested;
		//Logger.getInstance().log(message);
		// message = "Pieces currently downloading: " +
		// piecesCurrentlyDownloading.toString();
		// Logger.getInstance().log(message);

		if (!piecesInterested.isEmpty()) { // if there is some piece which can
											// be requested
			ArrayList<Integer> arr = new ArrayList<Integer>();
			for (pieceId = piecesInterested.nextSetBit(0); pieceId >= 0; pieceId = piecesInterested
					.nextSetBit(pieceId + 1)) {
				// check if this piece is not already currently downloading
				// add the piece to arraylist to get a random piece
				// if (piecesCurrentlyDownloading.get(pieceId) == -1 ||
				// piecesCurrentlyDownloading.get(pieceId) == requestFromPeerId
				// ) {
				arr.add(pieceId);
				// }
			}
			// Generate a random number if there exists a piece
			if (!arr.isEmpty()) {
				Random random = new Random();
				int index = random.nextInt(arr.size());
				pieceId = arr.get(index);
				// System.out.println("Random: " + pieceId);
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

		if (!MetaInfo.isCompletefile()
				&& MetaInfo.getnPieces() == getNumPiecesCompleted()) {
			String logMessage = "Peer " + MetaInfo.getPeerId()
					+ " has downloaded the complete file ";
			Logger.log(logMessage);

		        FileHandlingUtils fh = new FileHandlingUtils();
		        fh.finish();
			MetaInfo.setCompletefile(true);
		}

		if ((MetaInfo.getnPieces() == getNumPiecesCompleted())
				&& (MetaInfo.getNumPeers() == getNumPeersCompleted())) {
			System.out.println("All Peers completed: " + getNumPeersCompleted());
			MetaInfo.setShutDown(true);
			shutdown();
			return true;
		}
		return false;
	}

	public void shutdown() {
		// Assemble pieces
		//System.out.println("SHUTTING DOWN ");
		FileHandlingUtils fh = new FileHandlingUtils();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		// Shutdown executor service
		ExecutorPool.getInstance().getPool().shutdown();
		// Shut down all the sockets
		// Wait for some time to send all the packets in queue
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		//Logger.getInstance().close();
		Logger.close();
		Server.LISTENING = false;
		Iterator<Integer> itr = map.keySet().iterator();
		while (itr.hasNext()) {
			int peer = itr.next();
			PeerInfo peerInfo = map.get(peer);
			Socket socket = peerInfo.getSocket();
			try {
				socket.close();
			} catch (Exception e) {
			}
		}
		//fh.deletePieces();
		System.exit(1);
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

	public boolean isReadyToSendHave(int peerId2) {
		PeerInfo peerInfo = map.get(peerId2);
		if (isConnected.get(peerId2) && peerInfo.isBitFieldSent()
				&& peerId2 != MetaInfo.getPeerId()) {
			return true;
		}
		//String logMessage = "Sending have message failed for: " + peerId2
		//		+ " isConnected: " + isConnected.get(peerId2) + "isBitField: "
		//		+ peerInfo.isBitFieldSent();
		//Logger.getInstance().log(logMessage);
		return false;
	}

	public void handleChoke(int peerId2) {
		// Update unchokeme map and release any pieces blocked
		unchokedMeMap.put(peerId2, false);
		// release any pieces requested
		Iterator<Integer> itr = piecesCurrentlyDownloading.keySet().iterator();
		while (itr.hasNext()) {
			int piece = itr.next();
			int peer = piecesCurrentlyDownloading.get(piece);
			if (peer == peerId2) { // relesase the piece
				piecesCurrentlyDownloading.put(piece, -1);
			}
		}
	}

	/**
	 * Update the piece info for the corresponding peer
	 * 
	 * @param peerId2
	 * @param bs
	 */
	public void updatePeerBitset(int peerId2, BitSet bs) {
		PeerInfo peerInfo = map.get(peerId2);
		peerInfo.setPieceInfo(bs);
		peerInfo.updatePieceInterested();
		//Logger.getInstance().log(
		//		"Peer " + peerId2 + " bitset is: "
		//				+ peerInfo.getPieceInfo().toString());
		if (peerInfo.getNumPiecesInterested() == 0)
			numPeersCompleted++;
	}

	public boolean updateOwnBitSet(int pieceId, int peerId2) {
		// increase the counter of pieces downloaded
		int pieces = piecesDownloadedFrom.get(peerId2);
		piecesDownloadedFrom.put(peerId2, pieces + 1);

		piecesCurrentlyDownloading.put(pieceId, -1);
		if (!pieceInfo.get(pieceId)) { // check if the piece is not already
										// received from some other peer
			pieceInfo.set(pieceId); // In that case dont increment the counter
			setNumPiecesCompleted(getNumPiecesCompleted() + 1);

			//Logger.log(
			//		"Peer " + MetaInfo.getPeerId() + " bitset is: "
			//				+ getPieceInfo().toString());

			String logMessage = "Peer " + MetaInfo.getPeerId()
					+ " has downloaded the piece " + pieceId + " from "
					+ peerId2 + ". Now the number of pieces it has is "
					+ getNumPiecesCompleted();
			Logger.log(logMessage);

			if (getNumPiecesCompleted() == MetaInfo.getnPieces()) {
				numPeersCompleted++;
			}
			return true;
		}
		return false;
	}

	public void updatePeerBitset(int peerId, int pieceId) {
		PeerInfo info = map.get(peerId);

		if (!info.getPieceInfo().get(pieceId)) { // check if the piece is not
													// already
			// received from some other peer

			info.getPieceInfo().set(pieceId);
			info.updatePieceInterested();
			//Logger.getInstance().log(
			//		"Peer " + peerId + " bitset is: "
			//				+ info.getPieceInfo().toString()
			//				+ " num pieces interested: "
			//				+ info.getNumPiecesInterested()
			//				+ " peers completed: " + numPeersCompleted);
			if (info.getNumPiecesInterested() == 0) {
				//System.out.println("Complted: " + peerId + " total peerscomplete : " + numPeersCompleted);
				numPeersCompleted++;
			}
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
		if (pieceId != -1) {
			sendRequestMessage(peerId2, pieceId);
		}
	}

	public void sendRequestMessage(int peerId2, int pieceId) {
		if (!unchokedMeMap.get(peerId2))
			return;
		Request requestMessage = new Request(pieceId);
		SendMessage sendMessage = new SendMessage(peerId2,
				requestMessage.getBytes());
		ExecutorPool.getInstance().getPool().execute(sendMessage);
		piecesCurrentlyDownloading.put(pieceId, peerId2);
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

	public  void setNumPeersCompleted(int numPeersCompleted) {
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

	private int getNumPiecesCompleted() {

		int pieceId = -1;
		int piecesDownloaded = 0;
		for (pieceId = pieceInfo.nextSetBit(0); pieceId >= 0; pieceId = pieceInfo
				.nextSetBit(pieceId + 1)) {
			piecesDownloaded++;
		}
		numPiecesCompleted = piecesDownloaded;
		return numPiecesCompleted;
	}

	private void setNumPiecesCompleted(int numPiecesCompleted) {

		this.numPiecesCompleted = numPiecesCompleted;
	}

	public int getCount() {
		count++;
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public HashMap<Integer, Boolean> getIsConnected() {
		return isConnected;
	}

	public void setIsConnected(HashMap<Integer, Boolean> isConnected) {
		this.isConnected = isConnected;
	}

	public void downloadRatePrioritySelect() {
		HashMap<Integer, Boolean> newlySelectedNeighbor = new HashMap<Integer, Boolean>();
		ArrayList<Integer> interestedPeerList = new ArrayList<Integer>();
		HashMap<Integer, Boolean> currentlyInterested = getCurrentlyInterested();
		LinkedHashMap<Integer, Integer> sortedMap = new LinkedHashMap<Integer, Integer>();
		int k = MetaInfo.getNumPreferredNeighbours();
		int count = 0;
		StringBuilder sb = new StringBuilder();

		// Sorted map
		// Get the currently Interested neighbors in a ArrayList

		sortedMap = (LinkedHashMap<Integer, Integer>) sortByValue(piecesDownloadedFrom);
		Iterator<Integer> itr = sortedMap.keySet().iterator();
		while (itr.hasNext() && count < k) {
			int peerId = itr.next();
			if (currentlyInterested.get(peerId)
					&& peerId != MetaInfo.getPeerId() && isConnected.get(peerId)) {
				interestedPeerList.add(peerId);
			}
		//	piecesDownloadedFrom.put(peerId, 0);
		}

		int currentInterestedSize = interestedPeerList.size();
		if (currentInterestedSize > k) {
			itr = interestedPeerList.iterator();
			while (itr.hasNext() && count < k) {
				int peerId = itr.next();
				if (peerId == MetaInfo.getPeerId()) {
					System.out
							.println("********************************************* this is wrong************");
					continue; // should never reach here
				}
				if (!newlySelectedNeighbor.containsKey(peerId)) {
					newlySelectedNeighbor.put(peerId, true);
				}
				count++;
			}
		} else if (currentInterestedSize <= k && currentInterestedSize != 0) { // Add
																				// everyone
			for (int peerId : interestedPeerList) {
				newlySelectedNeighbor.put(peerId, true);
			}
		}

		for (int peerId : newlySelectedNeighbor.keySet()) {
			sb.append(peerId);
			sb.append(",");
		}

		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1); // to remove the last comma
		}

		// Log the newlySelected neighbor
		String logMessage = "Peer " + MetaInfo.getPeerId()
				+ " has the preferred neighbors: " + sb.toString();
		Logger.log(logMessage);

		// Iterate the current map and send choke and unchoke messages based on
		// newly selected map.
		HashMap<Integer, Boolean> oldMap = getPreferredNeighbors();
		itr = oldMap.keySet().iterator();
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

	public HashMap<Integer, Integer> getPiecesDownloadedFrom() {
		return piecesDownloadedFrom;
	}

	public void setPiecesDownloadedFrom(
			HashMap<Integer, Integer> piecesDownloadedFrom) {
		this.piecesDownloadedFrom = piecesDownloadedFrom;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(
			Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}
