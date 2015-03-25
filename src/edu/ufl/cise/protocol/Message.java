package edu.ufl.cise.protocol;

public abstract class Message {
	
	public static final String HEADER = "P2PFILESHARINGPROJ";
	
	public enum MessageType {
		CHOKE(0),
		UNCHOKE(1),
		INTERESTED(2), 
		NOT_INTERESTED(3),
		HAVE(4),
		BITFIELD(5),
		REQUEST(6),
		PIECE(7);
		
		private int val;
		
		private MessageType(int val){
			this.val = val;
		}
		
		public int getValue(){
			return this.val;
		}
	}
	
	public int byteArrayToInt(byte[] b) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i] & 0x000000FF) << shift;
		}
		return value;
	}

	public byte[] intToByteArray(int a) {
		byte[] ret = new byte[4];
		ret[0] = (byte) (a & 0xFF);
		ret[1] = (byte) ((a >> 8) & 0xFF);
		ret[2] = (byte) ((a >> 16) & 0xFF);
		ret[3] = (byte) ((a >> 24) & 0xFF);
		return ret;
	}

	
	
		
}
