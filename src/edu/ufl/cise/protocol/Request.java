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
		byte[] out = new byte[9];  // total length of packet which is 9bytes
		byte[] len = new byte[4];  // length bytes to be send before
		byte[] type = new byte[4];  // length of type
		byte[] pieceIndexBytes = new byte[4];  // pieceIndex in bytes
		
		len  = intToByteArray(5);  // 1 byte for message type + 4 bytes of piece index
		type = intToByteArray(mType.value);
		// copy the length bytes
		for(int i=0; i<4; i++){
			out[i] = len[i];
		}
		// copy the type bytes.
		// since LSB will be at the end of array get the last byte.
		out[4] = type[3];
//		System.out.println(out[4]);
		
		// copy the piece index bytes
		pieceIndexBytes = intToByteArray(pieceIndex);
		for(int i=0; i<4; i++){
			out[5+i] = pieceIndexBytes[i];
		}
/*		System.out.println("BYTES REQUEST: " );
		for(int i=0; i<9; i++){
			System.out.print(out[i]);
		}
		System.out.println();
		System.out.println(" ****** ");
*/		return out;
	}
	
	public String toString()
	{
		return "["+this.mType+"]";
	}

}
