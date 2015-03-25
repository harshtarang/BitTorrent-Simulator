package edu.ufl.cise.protocol;

public class HandshakeMessage extends Message {

	public static String HEADER = "P2PFILESHARINGPROJ";
	private byte zeroBits[] = new byte[10];
	private int peerId;

	public HandshakeMessage() {
	}

	public HandshakeMessage(int peerId) {
		this.peerId = peerId;
	}

	public byte[] getBytes(){
		byte[] out = new byte[32];
		byte[] header = new byte[18];
		byte[] peerIdBytes = new byte[4];
		header = HEADER.getBytes();
		peerIdBytes = intToByteArray(peerId);
		for( int i=0; i<18; i++){
			out[i] = header[i];
		}
		for( int i=18; i<28; i++){
			out[i] = 0x00;
		}
		for( int i=0; i<4; i++){
			out[28+i] = peerIdBytes[i];
		}
		return out;
	}
	
	public byte[] getZeroBits() {
		for( int i=0; i<10; i++){
			zeroBits[i] = 0x00;
		}
		return zeroBits;
	}

	public void setZeroBits(byte[] zeroBits) {
		this.zeroBits = zeroBits;
	}

	public int getPeerId() {
		return peerId;
	}

	public void setPeerId(int peerId) {
		this.peerId = peerId;
	}

}
