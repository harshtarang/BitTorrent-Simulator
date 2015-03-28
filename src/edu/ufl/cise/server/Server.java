package edu.ufl.cise.server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server implements Runnable{

	public static boolean LISTENING = true;
	int portNumber;
	
	public static void main(String[] args) throws IOException {
		Server server = new Server(9090);
		server.init();
	}
	
	public Server(int portNumber){
		this.portNumber = portNumber;
	}

	public int init() throws IOException {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			// Enter a log statement
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