package edu.ufl.cise.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerWorker implements Runnable {
	private Socket clientSocket = null;
	private PrintWriter out;
	private BufferedReader in;
	int peerId;
	int currPeerId;

	public ServerWorker(Socket socket) {
		this.currPeerId = PeerInfo.getInstance().getPeerId();
		this.clientSocket = socket;
		String hostName = socket.getInetAddress().getCanonicalHostName();
		System.out.println("Received request from : " + hostName);
	}

	public void run() {
		try {
			// out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			String input;
			while ((input = in.readLine()) != null) {
				// Pass the input to be processed by the protocol.
				processInput(input);
			}

		} catch (IOException e) {
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
		System.out.println("Received message on " + currPeerId + ":" + input);
		if (isNumeric(input)) {
			this.peerId = Integer.parseInt(input);
			PeerInfo.getInstance().getMap().get(this.peerId).setSocket(clientSocket);
			System.out.println("Updated socket on: " + currPeerId + " for: "
					+ this.peerId);
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
