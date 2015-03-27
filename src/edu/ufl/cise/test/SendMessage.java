package edu.ufl.cise.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SendMessage implements Runnable{

	int peerId;
	String message;
	
	public SendMessage(int peerId, String message){
		this.peerId = peerId;
		this.message = message;
	}
	
	public void run() {
		PrintWriter out;
		Socket socket = PeerInfo.getInstance().getMap().get(peerId).getSocket();
		try {
			System.out.println("Sending message: " + message);
			 out = new PrintWriter(socket.getOutputStream(), true);
			 out.write(message + "\n" );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
