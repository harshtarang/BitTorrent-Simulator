package edu.ufl.cise.protocol;

public class Piece extends Message {
	
	int index;
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public byte[] getPiece() {
		return piece;
	}

	public void setPiece(byte[] piece) {
		this.piece = piece;
	}

	byte[] piece;
	
	public Piece() {}

	public Piece(int index, byte[] piece) {
		this.index = index;
		this.piece = piece;
	}
}
