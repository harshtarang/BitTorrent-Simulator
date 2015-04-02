package edu.ufl.cise.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
	}
	
}