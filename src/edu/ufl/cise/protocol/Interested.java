package edu.ufl.cise.protocol;

public class Interested extends Message {

	MessageType mType = Message.MessageType.INTERESTED;
//	private final int  mType = 2;
	
	public Interested() {
	}

	public byte[] getBytes() {
		byte[] out = new byte[5];  // total length of packet to be send is 5bytes
		byte[] type = new byte[4];  // length of type
		byte[] len = new byte[4];  // length of packet to be send first
		
		len  = intToByteArray(1);  // 1 byte for message type 
		type = intToByteArray(mType.value);
		// copy the length bytes
		for(int i=0; i<4; i++){
			out[i] = len[i];
		}
		// copy the type bytes.
		// since LSB will be at the end of array get the last byte.
		out[4] = type[3];
/*		System.out.println(out[4]);
		
		System.out.println("BYTES :" );
		for( int i=0; i<5; i++){
			System.out.print(out[i]);
		}
		System.out.println("*****");
*/		return out;
	}

}
