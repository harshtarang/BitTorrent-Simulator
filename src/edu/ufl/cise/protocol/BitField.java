package edu.ufl.cise.protocol;

import java.util.BitSet;

import edu.ufl.cise.config.MetaInfo;
import edu.ufl.cise.util.Logger;

public class BitField extends Message {

	private byte[] bitField;
//	MessageType mType = Message.MessageType.BITFIELD;

	// private final int mType = 5;

	public BitField(byte[] bitArray) {
		mType = Message.MessageType.BITFIELD;
		bitField = bitArray;
	}

	public byte[] getBytes() {
		int bitFieldlen = bitField.length;
		byte[] out = new byte[bitFieldlen + 5];
		//byte[] type = new byte[4];
		byte[] lengthBytes;

		lengthBytes = intToByteArray(bitFieldlen + 1);
        //type = intToByteArray(mType.value);
		
        // copy the length bytes in out
        for (int i = 0; i < 4; i++) {
			out[i] = lengthBytes[i];
		}
		// copy the type bytes.
		// since LSB will be at the end of array get the last byte.
		//out[4] = type[3];
		out[4] = (byte)5;
        
		// copy the bitfield into out
		for (int i = 0; i < bitFieldlen; i++) {
			out[5 + i] = bitField[i];
		}
		//System.out.println("BitField message " );
		//for(int i=0; i<out.length; i++){
		//	System.out.print(out[i]);
		//}
		//System.out.println("****");
		return out;
	}

	public byte[] getBitField() {
		return bitField;
	}

	public void setBitField(byte[] bitField) {
		this.bitField = bitField;
	}

	public BitSet getBitSet() {
		int len = MetaInfo.getnPieces();
		//int len  =153;
		BitSet bs = new BitSet(len);
		int count = 0;
		for (int i = 0; i < bitField.length; i++) {
			byte b = bitField[i];
			for (int j = 7; j >= 0; j--) {
				if (count >= len) {
					break;
				}
				if ((b << j & 128) == 128) {
					bs.set(count);
				}
				count++;
			}
		}
		return bs;
	}
	
	public static void main(String args[]) {
/*		byte arr0, arr1, arr2, arr3;
		byte [] arr = new byte[4];
		arr0 = (byte)255;  // 0111111
		arr1 = (byte)128;    // 00000010
		arr2 = (byte)254;    // 01110001
		arr3 = (byte)255;
		arr[0] = arr0; arr[1] = arr1; arr[2] = arr2; arr[3] = arr3;
*/		
		byte []arr = new byte[20];
		for( int i=0; i<19; i++){
			arr[i] = (byte)255;
		}
		arr[19] = (byte) 1;
		BitField message = new BitField(arr);
		BitSet bs = message.getBitSet();
		for(int i=0; i<20; i++){
		//	System.out.println(i + ": " +bs.get(i));
		}
		byte[] newArr = bs.toByteArray();
		System.out.println("Length is: " + newArr.length);
		for( int i=0; i<newArr.length; i++){
			System.out.println(newArr[i]);
		}
		BitField message1 = new BitField(newArr);
		BitSet bs1 = message1.getBitSet();
		System.out.println(bs1);
		for(int i=0; i<30; i++){
			//System.out.println(i + ": " +bs1.get(i));
		}
		
		
	}
	
	@Override
	public String toString()
	{
		return "["+this.mType.value+"]";
	}

}
