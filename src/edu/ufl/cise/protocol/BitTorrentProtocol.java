package edu.ufl.cise.protocol;

public class BitTorrentProtocol implements Runnable {

	Message message;

	public BitTorrentProtocol(Message message) {
		this.message = message;
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
		// TODO Auto-generated method stub
		
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
