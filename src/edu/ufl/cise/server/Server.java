package edu.ufl.cise.server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

	public static boolean LISTENING = true;

	public static void main(String[] args) throws IOException {
		Server server = new Server();
		server.init(9090);
	}

	public int init(int portNumber) throws IOException {
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
			serverWorker.start(); 
		}
		serverSocket.close();
		return 0;
	}
	
}  