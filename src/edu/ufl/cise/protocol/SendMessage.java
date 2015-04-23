package edu.ufl.cise.protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import edu.ufl.cise.client.Peer;

public class SendMessage implements Runnable {

	int peerId;
	byte[] message;

	public SendMessage(int peerId, byte[] message) {
		this.peerId = peerId;
		this.message = message;
	}

	public void run() {
		OutputStream out;
		Socket socket = Peer.getInstance().getMap().get(peerId).getSocket();
		try {
			// System.out.println("Sending to peer "+ peerId+" message : " +
			// message[4]);
			out = socket.getOutputStream();
			synchronized (out) {
				out.write(message);
				//out.flush();
			}
		} catch (Exception e) {
			//e.printStackTrace();
			//System.out.println("Shutting down Message sender");
		}

	}

}
