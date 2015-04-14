package edu.ufl.cise.protocol;

import java.math.BigInteger;

import edu.ufl.cise.protocol.Message.MessageType;

public class HandshakeMessage extends Message {

	public MessageType mType = Message.MessageType.HANDSHAKE;
	public static String HEADER = "P2PFILESHARINGPROJ";
	private byte zeroBits[] = new byte[10];
	private int peerId;

	public HandshakeMessage() {
	}

	public HandshakeMessage(int peerId) {
		this.peerId = peerId;
		System.out.println("HANDHSHAKE: " + mType);
	}

	public byte[] getBytes(){
		System.out.println("Sending HEADER : " );
		byte[] out = new byte[32];
		byte[] header = new byte[18];
		byte[] peerIdBytes = new byte[4];
		
		header = HEADER.getBytes();
		peerIdBytes = intToByteArray(peerId);
		
		// Copy the header into out
		for( int i=0; i<18; i++){
			out[i] = header[i];
		}
		System.out.println("HEADER : " + new String(header));
		// Fill with zeros
		for( int i=18; i<28; i++){
			out[i] = 0x00;
		}
		
		for( int i=0; i<4; i++){
			out[28+i] = peerIdBytes[i];
		}
		System.out.println("HEADER : " + new BigInteger(peerIdBytes).intValue());
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
	
	public static void main(String args[]){
		HandshakeMessage message = new HandshakeMessage(12);
		byte[] arr = message.getBytes();
		for( int i=0; i<32; i++){
			
			System.out.print(arr[i]);
		}
	}
	
	public String toString()
	{
		return "["+this.mType+"]";
	}
	

}
