package edu.ufl.cise.protocol;

import java.math.BigInteger;

import edu.ufl.cise.protocol.BitField;
import edu.ufl.cise.protocol.Choke;
import edu.ufl.cise.protocol.Have;
import edu.ufl.cise.protocol.Interested;
import edu.ufl.cise.protocol.Message;
import edu.ufl.cise.protocol.NotInterested;
import edu.ufl.cise.protocol.Piece;
import edu.ufl.cise.protocol.Request;
import edu.ufl.cise.protocol.Unchoke;

public abstract class ReadWorker {

	
	public int getPeerId(byte[] temp){
		byte[] peer = new byte[4];
		peer[0] = temp[28]; peer[1] = temp[29]; 
		peer[2] = temp[30]; peer[3] = temp[31];
		return new BigInteger(peer).intValue();
	}

	public byte[] getHeader(byte[] firstFour, byte[] temp) {
		byte[] header = new byte[18];
		for(int i=0; i<4; i++){
			header[i] = firstFour[i];
		}
		for(int i=4; i<18; i++){
			header[i] = temp[i];
		}
		return header;
	}

	public boolean isHandShakeMessage(byte[] firstFour) {
		String firstFourBytes = new String(firstFour);
		System.out.println(" FIRSTFOUR " + firstFourBytes);
		if (firstFourBytes.equalsIgnoreCase("P2PF")) {
			return true;
		} else
			return false;
	}

	public Message returnMessageType(int len, byte[] input) {
		byte[] pieceIndex;
		byte[] bitArray ;
		byte[] mType = new byte[1];
		int pos = 5;
		
		mType[0] = input[4];
		int messageType = (int) mType[0];
		System.out.println("Message Type : " + messageType);
		Message response = null;
		if (messageType == Message.MessageType.CHOKE.value) {
			response = new Choke();
			System.out.println("Choke");
		} else if (messageType == Message.MessageType.UNCHOKE.value) {
			response = new Unchoke();
			System.out.println("Unchoke");
		} else if (messageType == Message.MessageType.INTERESTED.value) {
			response = new Interested();
			System.out.println("Interested");
		} else if (messageType == Message.MessageType.NOT_INTERESTED.value) {
			response = new NotInterested();
			System.out.println("Not interested");
		} else if (messageType == Message.MessageType.HAVE.value) {
			pieceIndex = getPieceIndex(pos, input);
			response = new Have(pieceIndex);
			System.out.println("Have");
		} else if (messageType == Message.MessageType.BITFIELD.value) {
			bitArray = getBitArray(input, pos, len-1);
			response = new BitField(bitArray);
			System.out.println("BitField");
		} else if (messageType == Message.MessageType.REQUEST.value) {
			pieceIndex = getPieceIndex(pos, input);
			response = new Request(pieceIndex);
			System.out.println("REquest");
		} else if (messageType == Message.MessageType.PIECE.value) {
			pieceIndex = getPieceIndex(pos, input);
			int index = new BigInteger(pieceIndex).intValue();
			len -= 5;
			pos += 4;
			byte[] piece = getBitArray(input, pos, len);
			response = new Piece(index, piece);
			System.out.println("Piece");
		}
		return response;
	}

	private byte[] getPieceIndex(int pos, byte[] input){
		byte[] pieceIndex = new byte[4];
		pieceIndex[3] = input[pos]; pieceIndex[2] = input[pos+1];
		pieceIndex[1] = input[pos+2]; pieceIndex[0] = input[pos+3];
		return pieceIndex;
	}
	
	private byte[] getBitArray(byte[] input, int pos, int len) {
		byte[] bitArray = new byte[len];
		for (int i = 0; i < len; i++) {
			bitArray[i] = input[pos + i];
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

	
}
