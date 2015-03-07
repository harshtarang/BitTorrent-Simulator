package edu.ufl.cise.server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

	public static boolean LISTENING = true;

	public static void main(String[] args) throws IOException {
		Server server = new Server();
		server.listen();
	}

	public int listen() throws IOException {
		ServerSocket serverSocket = null;
		int iPortNumber = 9090;
		try {
			serverSocket = new ServerSocket(iPortNumber);
		} catch (IOException e) {
			// Enter a log statement
			System.out.println("Something went wrong");
		}
		while (LISTENING) {
			ServerWorker serverWorker;
			System.out.println("*** Listen for a Client; at:" + iPortNumber
					+ " ***");
			serverWorker = new ServerWorker(serverSocket.accept());
			serverWorker.start(); 
		}
		serverSocket.close();
		return 0;
	}
	
}  