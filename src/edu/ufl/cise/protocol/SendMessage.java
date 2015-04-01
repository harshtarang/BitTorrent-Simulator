package edu.ufl.cise.protocol;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import edu.ufl.cise.client.Peer;

public class SendMessage implements Runnable{

	int peerId;
	byte[] message;
	
	public SendMessage(int peerId, byte[] message){
		this.peerId = peerId;
		this.message = message;
	}
	
	public void run() {
		PrintWriter out;
		Socket socket = Peer.getInstance().getMap().get(peerId).getSocket();
		try {
			System.out.println("Sending message: " + message);
			 out = new PrintWriter(socket.getOutputStream(), true);
			 out.println(message );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
