package edu.ufl.cise.protocol;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;

import edu.ufl.cise.client.Peer;
import edu.ufl.cise.config.MetaInfo;
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
			System.out.println("BITFIELD");
			handleBitField();
			break;
		case REQUEST:
			handleRequest();
			break;
		case PIECE:
			handlePiece();
			break;
		case HANDSHAKE:
			handleHandShakeMessage();
			break;
		}
	}

	private void handleHandShakeMessage() {
		// Check if handshake message already sent.
		boolean isHandshakeSent = Peer.getInstance().isHandshakeSent(peerId);
		if (isHandshakeSent) {
			// Send BITFIELD message
			Peer.getInstance().updateAndSendBitField(peerId);
		} else {
			// Send handshake message
			int currPeerId = MetaInfo.getPeerId();
			HandshakeMessage handShakeMessage1 = new HandshakeMessage(
					currPeerId);
			SendMessage message = new SendMessage(peerId,
					handShakeMessage1.getBytes());
			Peer.getInstance().updateAndSendHandshakeMessage(peerId, message);
			// Send BITFIELD message
			Peer.getInstance().updateAndSendBitField(peerId);
		}
	}

	private void handleBitField() {
		BitField bitField = (BitField) message;
		// Convert bytes to BitSet
		BitSet bs = bitField.getBitSet();
		// Set the pieceInfo and piecesInterested of corresponding peer
		Peer.getInstance().updateBitSets(peerId, bs);
		// Decide and send I/DI message
		Peer.getInstance().determineAndSendInterestedMessage(peerId);
	}

	private void handlePiece() {
		Piece pieceMessage = (Piece) message;
		FileHandlingUtils fh=new FileHandlingUtils();
		
		// Update own's bitset
		int pieceId = pieceMessage.getIndex();
		fh.writePiece(pieceId, pieceMessage.getPiece());
		Peer.getInstance().updateOwnBitSet(pieceId);
		// Broadcast Have message
		Iterator<Integer> itr = MetaInfo.getPeerList().iterator();
		while (itr.hasNext()) {
			int peerId1 = itr.next();
			if (peerId1 != MetaInfo.getPeerId()) { // Send a have message
				Have haveMessage = new Have(pieceId);
				SendMessage sendMessage = new SendMessage(peerId1,
						haveMessage.getBytes());
				ExecutorPool.getInstance().getPool().execute(sendMessage);
			}
		}
		// Check whether system needs to shutdown
		boolean isShutDown = Peer.getInstance().evaluateSystemShutDown();
		if (!isShutDown) {
			// Recompute whether to send I/DI message to some peers and send.
			Peer.getInstance().determineAndSendInterstedMessageToPeers();
			Peer.getInstance().determineAndSendPieceRequest(peerId);
		}
	}

	private void handleRequest() {
		// cast into Request message
		Request requestMessage = (Request) message;
		int pieceId = requestMessage.getPieceIndex();
		// Fetch the corresponding piece from hard disk and then finally check 
		// if the peer is unchoked or not. 
		// Thus reducing check by 1
		FileHandlingUtils util = new FileHandlingUtils();
		byte[] piece = util.getPiece(pieceId);
		// Create a piece message and send it
		Piece pieceMessage = new Piece(pieceId, piece);
		Peer.getInstance().sendPieceMessage(peerId, pieceMessage);
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
			Peer.getInstance().determineAndSendInterestedMessage(peerId);
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
		Peer.getInstance().getUnchokedMeMap().put(peerId, false);
	}

	private void handleUnChoke() {
		// Update unchoke map
		Peer.getInstance().getUnchokedMeMap().put(peerId, true);
		// Select a random piece to request.
		Peer.getInstance().determineAndSendPieceRequest(peerId);
	}

}
