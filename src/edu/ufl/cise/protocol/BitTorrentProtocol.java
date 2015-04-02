package edu.ufl.cise.protocol;

import edu.ufl.cise.client.Peer;
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
			break;

		}
	}

	private void handleBitField() {
		// TODO Auto-generated method stub
		
	}

	private void handlePiece() {
		// TODO Auto-generated method stub
		
	}

	private void handleRequest() {
		// cast into Request message
		Request requestMessage = (Request)message;
		int pieceId = requestMessage.getPieceIndex();
		// Check if the peer is unchoked or not
		if(Peer.getInstance().isUnchoked(peerId)){
			// Fetch the corresponding piece from hard disk
			FileHandlingUtils util = new FileHandlingUtils();
			byte[] piece = util.getPiece(pieceId);
			// Create a piece message and send it
			Piece pieceMessage = new Piece(pieceId, piece);
			SendMessage sendMessage = new SendMessage(peerId, pieceMessage.getBytes());
			ExecutorPool.getInstance().getPool().execute(sendMessage);
		}
	}

	private void handleHave() {
		// TODO Auto-generated method stub
		
	}

	private void handleNotInterested() {
		// TODO Auto-generated method stub
		
	}

	private void handleInterested() {
		// TODO Auto-generated method stub
		
	}

	private void handleUnChoke() {
		// TODO Auto-generated method stub
		
	}

	private void handleChoke() {
		// TODO Auto-generated method stub
		
	}

}
