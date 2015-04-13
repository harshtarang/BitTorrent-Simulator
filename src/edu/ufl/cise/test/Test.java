package edu.ufl.cise.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.Random;

public class Test{

	public static void main(String args[]){
		FileOutputStream fout = null;
		FileInputStream fin1 = null;
		FileInputStream fin2 = null;
		byte[] bytes = null;
		try {
			fout = new FileOutputStream("c.txt");
			fin1 = new FileInputStream("a.txt");
			fin2 = new FileInputStream("b.txt");
			bytes = new byte[7];
			fin1.read(bytes);
			fout.write(bytes);
			bytes = new byte[5];
			fin2.read(bytes);
			fout.write(bytes);
			fin1.close();
			fin2.close();
			fout.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int a = 3;
		int b = 2;
		float c = (float)a/(float)b;
		int size = (int) Math.ceil(c);
		//System.out.println(size);
		BitSet bs = new BitSet(size);
		bs.flip(0, 2);
		//System.out.println(bs.get(0) + " " + bs.get(1) + " " + " " + bs.get(2) + " " +  bs.size());
		byte arr[] = intToByteArray(3);
		System.out.println(arr[0]);
		System.out.println(arr[1]);
		System.out.println(arr[2]);
		System.out.println(arr[3]);
		System.out.println("*****");
		//System.out.println(new BigInteger(arr).intValue());
		System.out.println(byteArrayToInt(arr));
	}
	
	public static byte[] intToByteArray(int a) {
		byte[] ret = new byte[4];
		ret[3] = (byte) (a & 0xFF);
		ret[2] = (byte) ((a >> 8) & 0xFF);
		ret[1] = (byte) ((a >> 16) & 0xFF);
		ret[0] = (byte) ((a >> 24) & 0xFF);
		return ret;
	}

	public static int byteArrayToInt(byte[] b) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i] & 0x000000FF) << shift;
		}
		return value;
	}

	
}