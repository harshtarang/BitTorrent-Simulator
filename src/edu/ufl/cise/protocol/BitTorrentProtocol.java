package edu.ufl.cise.protocol;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;

import edu.ufl.cise.client.Peer;
import edu.ufl.cise.test.PeerInfo;
import edu.ufl.cise.util.ExecutorPool;
import edu.ufl.cise.util.FileHandlingUtils;

public class BitTorrentProtocol implements Runnable {

	Message message;
	int peerId;

	public BitTorrentProtocol(Message message, int peerId) {
		this.message = message;
		this.peerId = peerId;
	}

	public void run() {
		switch (message.mType) {

		case CHOKE:
			handleChoke();
			break;
		case UNCHOKE:
			handleUnChoke();
			break;
		case INTERESTED:
			handleInterested();
			break;
		case NOT_INTERESTED:
			handleNotInterested();
			break;
		case HAVE:
			handleHave();
			break;
		case BITFIELD:
			handleBitField();
			break;
		case REQUEST:
			handleRequest();
			break;
		case PIECE:
			handlePiece();
			break;
		default:
			handleHandShakeMessage();
		}
	}

	private void handleHandShakeMessage() {
		// Check if handshake message already sent.
		boolean isHandshakeSent = Peer.getInstance().isHandshakeSent(peerId);
		if(isHandshakeSent){
			// Send BITFIELD message
			BitField bitFieldMessage = Peer.getInstance().getBitFieldMessage();
			SendMessage message = new SendMessage(peerId, bitFieldMessage.getBytes());
			ExecutorPool.getInstance().getPool().execute(message);
		}
		else{
			//Send handshake message 
			int currPeerId = PeerInfo.getInstance().getPeerId(); // Todo : put peerId in metainfo
			HandshakeMessage handShakeMessage1 = new  HandshakeMessage(currPeerId);
			SendMessage message = new SendMessage(peerId, handShakeMessage1.getBytes());
			ExecutorPool.getInstance().getPool().execute(message);
			Peer.getInstance().updateHandshakeSent(peerId);
			// Send BITFIELD message
			BitField bitFieldMessage = Peer.getInstance().getBitFieldMessage();
			message = new SendMessage(peerId, bitFieldMessage.getBytes());
			ExecutorPool.getInstance().getPool().execute(message);
		}
	}

	private void handleBitField() {
		BitField bitField = (BitField) message;
		// Convert bytes to BitSet
		BitSet bs = bitField.getBitSet();
		// Set the pieceInfo and piecesInterested of corresponding peer 
		Peer.getInstance().updateBitSets(peerId, bs);
		// Decide and send I/DI message
		Peer.getInstance().determineToSendInterestedMessage(peerId);
	}

	private void handlePiece() {
		Piece pieceMessage = (Piece) message;
		// Update own's bitset
		int pieceId = pieceMessage.getIndex();
		Peer.getInstance().getPieceInfo().set(pieceId);
		// Broadcast Have message
		HashMap<Integer, Boolean> peers = Peer.getInstance()
				.getCurrentlyInterested();
		Iterator<Integer> itr = peers.keySet().iterator();
		while (itr.hasNext()) {
			int peerId1 = itr.next();
			if (peerId1 != peerId) { // Send a have message
				Have haveMessage = new Have(pieceId);
				SendMessage sendMessage = new SendMessage(peerId,
						haveMessage.getBytes());
				ExecutorPool.getInstance().getPool().execute(sendMessage);
			}
		}
		// Check whether system needs to shutdown
		boolean isShutDown = Peer.getInstance().evaluateSystemShutDown();
		if (!isShutDown) {
			// Recompute whether to send I/DI message to some peers and send.
			Peer.getInstance().determineToSendInterstedMessageToPeers();
			
			Peer.getInstance().determineAndSendPieceRequest(peerId);
		}
	}

	private void handleRequest() {
		// cast into Request message
		Request requestMessage = (Request) message;
		int pieceId = requestMessage.getPieceIndex();
		// Check if the peer is unchoked or not
		if (Peer.getInstance().isUnchoked(peerId)) {
			// Fetch the corresponding piece from hard disk
			FileHandlingUtils util = new FileHandlingUtils();
			byte[] piece = util.getPiece(pieceId);
			// Create a piece message and send it
			Piece pieceMessage = new Piece(pieceId, piece);
			SendMessage sendMessage = new SendMessage(peerId,
					pieceMessage.getBytes());
			ExecutorPool.getInstance().getPool().execute(sendMessage);
		}
	}

	private void handleHave() {
		// cast into Have message
		Have haveMessage = (Have) message;
		int pieceIndex = haveMessage.getPieceIndex();
		// Update bit set of corresponding peer
		Peer.getInstance().updatePeerBitset(peerId, pieceIndex);
		// Evaluate if system needs to shutdown
		boolean isShutDown = Peer.getInstance().evaluateSystemShutDown();
		// if not then evaluate whether to send I/DI message
		if (!isShutDown) {
			// Decide and send I/DI message
			Peer.getInstance().determineToSendInterestedMessage(peerId);
		}
	}

	private void handleNotInterested() {
		// Update the Not Interested map
		Peer.getInstance().getCurrentlyInterested().put(peerId, false);
		// Check if unchoked choke it.
		Peer.getInstance().updateAndSendChoke(peerId);
	}

	private void handleInterested() {
		// Update the interested map
		Peer.getInstance().getCurrentlyInterested().put(peerId, true);
	}

	private void handleChoke() {
		// Update unchoke map
		Peer.getInstance().getUnchokedMeMap().put(peerId, true);
	}

	private void handleUnChoke() {
		// Update unchoke map
		Peer.getInstance().getUnchokedMeMap().put(peerId, true);
		// Select a random piece to request.
		int pieceId = Peer.getInstance().getRandomPieceToRequest(peerId);
		if (pieceId != -1) { // if there is such a piece
			// Send a request message
			Request requestMessage = new Request(pieceId);
			SendMessage sendMessage = new SendMessage(peerId,
					requestMessage.getBytes());
			ExecutorPool.getInstance().getPool().execute(sendMessage);
		}
	}

}
