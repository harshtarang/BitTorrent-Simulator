package edu.ufl.cise.protocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class HandshakeMessage extends Message{
	
	public static String HEADER = "P2PFILESHARINGPROJ";
	byte zeroBits[] = new byte[10];
	int peerId;
	
	public HandshakeMessage(byte[] in) throws IOException{
		byte[] b = new byte[18];
		// Read the header
		String header = new String(b, 0, 18);
		// Read the zero bits
		b = new byte[10];
		
		// Read the peerId
		b = new byte[4];
		peerId = new BigInteger(b).intValue();
	}
		
	public boolean isValid(String header){
		return header == HEADER;
	}
	
}
