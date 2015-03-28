package edu.ufl.cise.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientWorker implements Runnable {
	private Socket socket = null;
	private PrintWriter out;
	private BufferedReader in;
	int port;
	int peerId;
	int currPeerId;

	public ClientWorker(int peerId, int port) throws UnknownHostException,
			IOException {
		this.peerId = peerId;
		this.currPeerId = PeerInfo.getInstance().getPeerId();
		this.port = port;
		
		System.out.println("Peer: " + currPeerId +" connecting on port: " + port);
		socket = new Socket("localhost", port);
		System.out.println(socket.isConnected());

		PeerInfo.getInstance().updateSocket(peerId, socket);
		System.out.println("Sending message : " + currPeerId + " to: " + peerId);
		PeerInfo.getInstance().updateFirstMessageSent(peerId);

		SendMessage message = new SendMessage(peerId, ""+currPeerId);
		ExecutorPool.getInstance().getPool().execute(message);
	}

	public void run() {
		try {
			// out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			String input;
			while ((input = in.readLine()) != null) {
				processInput(input);
			}

		} catch (IOException e) {
			// Add a log statement
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}
	}

	private void processInput(String input) {
		//System.out.println("processinput");
		System.out.println("Received message on " + currPeerId + ":" + input);
		if (isNumeric(input)) {
			int peerIdReceived = Integer.parseInt(input);
			/*if (peerIdReceived == peerId) {
				System.out.println("Received back response for: " + currPeerId
						+ " from: " + peerId);
			}*/
		}
		Protocol protocol = new Protocol(currPeerId, peerId, input);
		ExecutorPool.getInstance().getPool().execute(protocol);
	}

	private boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

}
