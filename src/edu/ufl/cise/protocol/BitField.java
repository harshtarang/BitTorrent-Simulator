package edu.ufl.cise.protocol;

public class BitField extends Message {

	byte[] bitField;
	int mType = Message.MessageType.BITFIELD.getValue();
	
	public BitField(byte[] bitArray) {
		bitField = bitArray;
	}
	
	public byte[] getBytes(){
		int bitFieldlen = bitField.length;
		byte[] out = new byte[bitFieldlen +5];
		byte[] type = new byte[1];
		type = intToByteArray(mType);

		byte[] lengthBytes  = intToByteArray(bitFieldlen + 5);
		for(int i=0; i<4; i++){
			out[i] = lengthBytes[i];
		}
		out[4] = type[0];
		
		for(int i=0; i<bitFieldlen; i++){
			out[5+i] = bitField[i];
		}
		return out;
	}
	
	public static void main(String args[]){
	}
}
