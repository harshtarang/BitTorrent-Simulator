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
		int bitFieldlen = piece.length;
		byte[] out = new byte[bitFieldlen + 9]; // 4 for length + 1 for type + 4 for piece number
		byte[] type = new byte[1];
		byte[] lengthBytes  = intToByteArray(bitFieldlen + 5);
		byte[] indexBytes  = intToByteArray(index);
		
		type = intToByteArray(mType.value);

		for(int i=0; i<4; i++){
			out[i] = lengthBytes[i];
		}
		out[4] = type[0];
		
		for(int i=0; i<4; i++){
			out[i+5] = indexBytes[i];
		}
		
		for(int i=0; i<bitFieldlen; i++){
			out[i+9] = piece[i];
		}
		
		return out;
	}

	public static void main(String args[]){
		
	}
	
	
}
