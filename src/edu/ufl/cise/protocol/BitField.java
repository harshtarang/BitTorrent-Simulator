package edu.ufl.cise.protocol;

import java.util.BitSet;

import edu.ufl.cise.config.MetaInfo;

public class BitField extends Message {

	private byte[] bitField;
	MessageType mType = Message.MessageType.BITFIELD;

	// private final int mType = 5;

	public BitField(byte[] bitArray) {
		bitField = bitArray;
	}

	public byte[] getBytes() {
		int bitFieldlen = bitField.length;
		byte[] out = new byte[bitFieldlen + 5];
		byte[] type = new byte[4];
		byte[] lengthBytes;

		lengthBytes = intToByteArray(bitFieldlen + 5);
        type = intToByteArray(mType.value);
		
        // copy the length bytes in out
        for (int i = 0; i < 4; i++) {
			out[i] = lengthBytes[i];
		}
		// copy the type bytes.
		// since LSB will be at the end of array get the last byte.
		out[4] = type[3];
		
		// copy the bitfield into out
		for (int i = 0; i < bitFieldlen; i++) {
			out[5 + i] = bitField[i];
		}
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
		//int len = 20;
		BitSet bs = new BitSet(len);
		int count = 0;
		for (int i = 0; i < bitField.length; i++) {
			byte b = bitField[i];
			for (int j = 0; j < 8; j++) {
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
		byte arr0, arr1, arr2;
		byte [] arr = new byte[3];
		arr0 = (byte)127;  // 0111111
		arr1 = (byte)2;    // 00000010
		arr2 = (byte)112;    // 01110001
		arr[0] = arr0; arr[1] = arr1; arr[2] = arr2;
		BitField message = new BitField(arr);
		BitSet bs = message.getBitSet();
		//for(int i=0; i<20; i++){
		//	System.out.println(bs.get(i));
		//}
		
	}
	
	@Override
	public String toString()
	{
		return "["+this.mType.value+"]";
	}

}
