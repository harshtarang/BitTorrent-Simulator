package edu.ufl.cise.protocol;

import java.io.UnsupportedEncodingException;

import sun.security.util.BigInt;

public class BitTorrentProtocol implements Runnable {

	byte[] input;
	
	public BitTorrentProtocol(byte[] input){
		this.input = input;
	}
	
	public void processInput(byte[] input){
		// Determine the message type and construct it
		isHandShakeMessage();
		// Based on message state update PeerInfo state
		// Determine the events to be triggered
		// Execute the events
		
	}
	
	public boolean isHandShakeMessage()  {
		byte[] data = new byte[4];
		data[0] = input[0]; data[1] = input[1];
		data[2] = input[2]; data[3] = input[3];
		String firstFourBytes = null;
		firstFourBytes = new String(data);
		if (isNumeric(firstFourBytes)) {
			return false;
		}
		else return true;
	}

	public Message returnMessageType() throws NumberFormatException, UnsupportedEncodingException{
		// Determine message length
		byte[] length = new byte[4];
		length = getPieceIndex(0);
		int len = new BigInt(length).toInt() -1;
		
		// Determine message type
		byte[] mType = new byte[1];
		mType[0] = input[4];
		int messageType = Integer.parseInt(new String(mType, "UTF-8"));
		int pos = 5;

		Message response = null;
		if (messageType == Message.MessageType.CHOKE.getValue()) {
			response = new Choke();
		}
		else if (messageType == Message.MessageType.UNCHOKE.getValue()) {
			response = new Unchoke();
		} else if (messageType == Message.MessageType.INTERESTED.getValue()) {
			response = new Interested();
		} else if (messageType == Message.MessageType.NOT_INTERESTED.getValue()) {
			response = new NotInterested();
		} else if (messageType == Message.MessageType.HAVE.getValue()) {
			byte[] pieceIndex = new byte[4];
			pieceIndex = getPieceIndex(pos);
			response = new Have(pieceIndex);
		} else if (messageType == Message.MessageType.BITFIELD.getValue()) {
			byte[] bitArray = new byte[len];
			bitArray = getBitArray(pos, len);
			response = new BitField(bitArray);
		} else if (messageType == Message.MessageType.REQUEST.getValue()) {
			byte[] pieceIndex = new byte[4];
			pieceIndex = getPieceIndex(pos);
			response = new Request(pieceIndex);
		} else if (messageType == Message.MessageType.PIECE.getValue()) {
			byte[] pieceIndex = new byte[4];
			pieceIndex = getPieceIndex(pos);
			int index = new BigInt(pieceIndex).toInt();
			len--;
			pos += 4;
			byte[] piece = getBitArray(pos, len);
			response = new Piece(index, piece);
		}
		return response;
	}
	
	private byte[] getPieceIndex(int pos){
		byte[] pieceIndex = new byte[4];
		pieceIndex[0] = input[pos]; pieceIndex[1] = input[pos+1];
		pieceIndex[2] = input[pos+2]; pieceIndex[3] = input[pos+3];
		return pieceIndex;
	}
	
	private byte[] getBitArray(int pos, int len){
		byte[] bitArray = new byte[len];
		for( int i=0; i<len; i++ ){
			bitArray[i] = input[pos+i];
		}
		return bitArray;
	}
	
	private boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public void run() {
		
	}

}
