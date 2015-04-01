package edu.ufl.cise.protocol;

public class Choke extends Message {

	int mType = Message.MessageType.CHOKE.getValue();
	
	public Choke() {}

	public byte[] getBytes(){
		byte[] out = new byte[5];
		byte[] len = new byte[4];
		byte[] type = new byte[1];
		len  = intToByteArray(1);
		type = intToByteArray(mType);
		for(int i=0; i<4; i++){
			out[i] = len[i];
		}
		out[4] = type[0];
		return out;
	}
	
}
