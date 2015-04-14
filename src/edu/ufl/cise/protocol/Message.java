package edu.ufl.cise.protocol;

public abstract class Message {

//	Type type;
	public MessageType mType;

//	public enum Type {
//		CHOKE, UNCHOKE, INTERESTED, NOT_INTERESTED, HAVE, BITFIELD, REQUEST, PIECE
//	}
	
	public static final int PIECE_INDEX_LENGTH = 4;
	public static final int PEER_ID_LENGTH = 4;
	public static final int MESSAGE_TYPE_LENGTH = 1;
	public static final int HAVE_LENGTH = 5;
	public static final int REQUEST_LENGTH = 5;

	public enum MessageType {
		CHOKE(0),
		UNCHOKE(1),
		INTERESTED(2),
		NOT_INTERESTED(3),
		HAVE(4),
		BITFIELD(5),
		REQUEST(6),
		PIECE(7),
		HANDSHAKE(8);
		
		public int value;
		
		MessageType(int val){
			this.value = val;
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
		ret[3] = (byte) (a & 0xFF);
		ret[2] = (byte) ((a >> 8) & 0xFF);
		ret[1] = (byte) ((a >> 16) & 0xFF);
		ret[0] = (byte) ((a >> 24) & 0xFF);
		return ret;
	}

	public MessageType getmType() {
		return mType;
	}

	public void setmType(MessageType mType) {
		this.mType = mType;
	}
	
	@Override
	public String toString()
	{
		return "["+this.mType+"]";
	}

}
