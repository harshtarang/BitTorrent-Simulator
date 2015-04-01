package edu.ufl.cise.protocol;

import java.math.BigInteger;

public class Request extends Message {

	int pieceIndex;
	MessageType mType = Message.MessageType.REQUEST;
//	private final int  mType = 6;

	public Request() {
	}

	public Request(int pieceIndex) {
		this.pieceIndex = pieceIndex;
	}

	public Request(byte[] in) {
		int index = new BigInteger(in).intValue();
		this.pieceIndex = index;
	}
	
	public int getPieceIndex() {
		return pieceIndex;
	}

	public void setPieceIndex(int pieceIndex) {
		this.pieceIndex = pieceIndex;
	}

	public byte[] getBytes() {
		byte[] out = new byte[9];
		byte[] len = new byte[4];
		byte[] type = new byte[5];
		byte[] pieceIndexBytes = new byte[4];
		len = intToByteArray(1);
		type = intToByteArray(mType.value);
		for (int i = 0; i < 4; i++) {
			out[i] = len[i];
		}
		out[4] = type[0];
		pieceIndexBytes = intToByteArray(pieceIndex);
		for (int i = 0; i < 4; i++) {
			out[5 + i] = pieceIndexBytes[i];
		}
		return out;
	}

}
