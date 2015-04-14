package edu.ufl.cise.protocol;

public class Piece extends Message {
	
	int index;
	byte[] piece;
	MessageType mType = Message.MessageType.PIECE;
//	private final int  mType = 7;
	
	public Piece() {}

	public Piece(int index, byte[] piece) {
		this.index = index;
		this.piece = piece;
	}

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

	public byte[] getBytes(){
		int pieceLen = piece.length;
		byte[] out = new byte[pieceLen + 9]; // 4 for length + 1 for type + 4 for piece number
		byte[] type = new byte[4];
		
		byte[] lengthBytes  = intToByteArray(pieceLen + 5);
		byte[] indexBytes  = intToByteArray(index); // piece Index
		
		type = intToByteArray(mType.value);

        // copy the length bytes in out
        for (int i = 0; i < 4; i++) {
			out[i] = lengthBytes[i];
		}
		// copy the type bytes.
		// since LSB will be at the end of array get the last byte.
		out[4] = type[3];
		
		// copy the piece index
		for(int i=0; i<4; i++){
			out[i+5] = indexBytes[i];
		}
		// copy the actual piece
		for(int i=0; i<pieceLen; i++){
			out[i+9] = piece[i];
		}
		return out;
	}

	public static void main(String args[]){
		
	}
	
	public String toString()
	{
		return "["+this.mType+"]";
	}
	
}
