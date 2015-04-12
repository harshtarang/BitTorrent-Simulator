package edu.ufl.cise.test;

import java.io.IOException;
import java.io.OutputStream;

public class SendMessage implements Runnable{

	int peerId;
	byte[] message;
	
	public SendMessage(int peerId, byte[] message){
		this.peerId = peerId;
		this.message = message;
	}
	
	public void run() {
		OutputStream out = PeerInfo.getInstance().getMap().get(peerId).getOut();
		try {
			 synchronized (out) {
				 out.write(message);
				 System.out.println(" SendEMssage " +  new String(message));
				 out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
