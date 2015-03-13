package edu.ufl.cise.protocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class HandshakeMessage extends Message{
	
	public static String HEADER = "P2PFILESHARINGPROJ";
	private byte zeroBits[] = new byte[10];
	private int peerId;
	
	public HandshakeMessage(byte[] in) throws IOException
	{
		
		// Read the header
		String header = new String(in, 0, 18);
		// Read the zero bits
		byte[] zb = new byte[10];
		zb = new byte[10];
		for(int i=0;i<10;i++)
		{
			zb[i]=in[i+17];
		}
		
		setZeroBits(zb);
		// Read the peerId
		
		byte[] pId = new byte[4];
		for(int i=0;i<4;i++)
		{
			pId[i]=in[i+27];
		}
		
		setPeerId(new BigInteger(pId).intValue());
	}
		
	public HandshakeMessage() {
	}

	public HandshakeMessage(int peerId) {
		this.peerId = peerId;
	}

	public boolean isValid(String header)
	{
		return header == HEADER;
	}
	
	

	public byte[] getZeroBits() 
	{
		return zeroBits;
	}

	public void setZeroBits(byte[] zeroBits) 
	{
		this.zeroBits = zeroBits;
	}

	public int getPeerId() 
	{
		return peerId;
	}

	public void setPeerId(int peerId) 
	{
		this.peerId = peerId;
	}

	public static int byteArrayToInt(byte[] b) 
	{
	    int value = 0;
	    for (int i = 0; i < 4; i++) {
	        int shift = (4 - 1 - i) * 8;
	        value += (b[i] & 0x000000FF) << shift;
	    }
	    return value;
	}

	public static byte[] intToByteArray(int a)
	{
	    byte[] ret = new byte[4];
	    ret[0] = (byte) (a & 0xFF);   
	    ret[1] = (byte) ((a >> 8) & 0xFF);   
	    ret[2] = (byte) ((a >> 16) & 0xFF);   
	    ret[3] = (byte) ((a >> 24) & 0xFF);
	    return ret;
	}
}
