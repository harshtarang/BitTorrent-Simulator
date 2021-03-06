package edu.ufl.cise.protocol;

public class Choke extends Message {

	//MessageType mType = Message.MessageType.CHOKE;
//	private final int  mType = 0;
	
	public Choke() {
		mType = Message.MessageType.CHOKE;
	}

	public byte[] getBytes(){
		byte[] out = new byte[5];  // total length of packet to be send is 5bytes
		//byte[] type = new byte[4];  // length of type
		byte[] len = new byte[4];  // length of packet to be send first
		
		len  = intToByteArray(1);  // 1 byte for message type 
		//type = intToByteArray(mType.value);
		// copy the length bytes
		for(int i=0; i<4; i++){
			out[i] = len[i];
		}
		// copy the type bytes.
		// since LSB will be at the end of array get the last byte.
		//out[4] = type[3];
		out[4] = (byte)0;
/*		System.out.println(out[4]);
		
		System.out.println("BYTES :" );
		for( int i=0; i<5; i++){
			System.out.print(out[i]);
		}
		System.out.println("*****");
*/		return out;
	}
	
	public String toString()
	{
		return "["+this.mType+"]";
	}
	
}
