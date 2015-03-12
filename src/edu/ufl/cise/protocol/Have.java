package edu.ufl.cise.protocol;

import java.math.BigInteger;

public class Have extends Message
{
	private int pieceIndex;

	public int getPieceIndex() 
	{
		return pieceIndex;
	}

	public void setPieceIndex(int pieceIndex) 
	{
		this.pieceIndex = pieceIndex;
	}
	
	public Have(byte[] in)
	{
		int pieceIndex=new BigInteger(in).intValue();
		setPieceIndex(pieceIndex);
	}
}
