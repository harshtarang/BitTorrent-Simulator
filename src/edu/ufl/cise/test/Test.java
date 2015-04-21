package edu.ufl.cise.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
/*		System.out.println(arr[0]);
		System.out.println(arr[1]);
		System.out.println(arr[2]);
		System.out.println(arr[3]);
		System.out.println("*****");
		//System.out.println(new BigInteger(arr).intValue());
		System.out.println(byteArrayToInt(arr));
*/
	    Base base = null;
	    base = new SubClass();
	    test(base);
	    HashMap<Integer, Integer> map =new HashMap<Integer, Integer>();
	     map.put(1, 5); map.put(3, 0); map.put(4, 8);
	     map.put(2, 6); map.put(5, 8); map.put(6, 0);
	     LinkedHashMap<Integer, Integer> sortedMap = new LinkedHashMap<Integer, Integer>();
	     sortedMap = (LinkedHashMap<Integer, Integer>) sortByValue(map);
	     System.out.println(sortedMap);
	     map.put(1, 8); map.put(3, 9); map.put(4, 2);
	     sortedMap = (LinkedHashMap<Integer, Integer>) sortByValue(map);
	     System.out.println(sortedMap);
	     
	    
	}
	
	public static void test(Base base){
		SubClass sub = (SubClass) base;
		System.out.println(sub.a);
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
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(
			Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	
}