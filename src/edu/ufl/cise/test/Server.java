package edu.ufl.cise.test;

import java.io.IOException;
import java.net.ServerSocket;

public class Server implements Runnable {

	public static boolean LISTENING = true;

	int portNumber;
	
	public Server(int portNumber){
		this.portNumber = portNumber;
	}
	
	public int init() throws IOException {
		ServerSocket serverSocket = null;
		
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.out.println("Something went wrong");
		}
		while (LISTENING) {
			ServerWorker serverWorker;
			System.out.println("*** Listen for a Client; at:" + portNumber
					+ " ***");
			serverWorker = new ServerWorker(serverSocket.accept());
			new Thread(serverWorker).start(); 
		}
		serverSocket.close();
		return 0;
	}

	public void run() {
		try {
			init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}  