package edu.ufl.cise.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import edu.ufl.cise.config.Logger;
import edu.ufl.cise.protocol.Message;

public class Client {

	public static void init() {
		ClientWorker worker = new ClientWorker(9090, "localhost");
		worker.run();
	}

	public static void sendMessage(Message message, String hostName, int port) {
		PrintWriter out = null;
		try {
			try {
				Socket socket = new Socket(hostName, port);
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(message);
				socket.close();
			} catch (Exception e) {
				Logger.getInstance().log(hostName, "");
			}
			out.close();
		} 
		finally {
			if (out != null)
				out.close();
		}
	}
	
	public static void main(String args[]){
		Client.init();
	}

}
