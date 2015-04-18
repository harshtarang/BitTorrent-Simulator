package edu.ufl.cise.protocol;

public class NotInterested extends Message {

//	MessageType mType = Message.MessageType.NOT_INTERESTED;
//	private final int  mType = 3;
	
	public NotInterested() {
		mType = Message.MessageType.NOT_INTERESTED;
	}

	public byte[] getBytes() {
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
		out[4] = (byte)3;
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
