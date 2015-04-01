package edu.ufl.cise.protocol;

public class Interested extends Message {

	MessageType mType = Message.MessageType.INTERESTED;
//	private final int  mType = 2;
	
	public Interested() {
	}

	public byte[] getBytes() {
		byte[] out = new byte[5];
		byte[] len = new byte[4];
		byte[] type = new byte[1];
		len = intToByteArray(1);
		type = intToByteArray(mType.value);
		for (int i = 0; i < 4; i++) {
			out[i] = len[i];
		}
		out[4] = type[0];
		return out;
	}

}
