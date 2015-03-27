package edu.ufl.cise.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.ufl.cise.config.MetaInfo;
import edu.ufl.cise.protocol.BitTorrentProtocol;
import edu.ufl.cise.protocol.HandshakeMessage;

public class ClientWorker implements Runnable {

	Socket clientSocket;
	OutputStream out;
	InputStream in;
	int peerID;
	int port;
	String hostName;

	public ClientWorker(int peerId, int port, String hostName) {
		this.peerID = peerId;
		this.port = port;
		this.hostName = hostName;
	}

	public void run() {
		try {
			clientSocket = new Socket(hostName, port);
			out = clientSocket.getOutputStream();
			in = clientSocket.getInputStream();
			// Create a handshake message. 
			HandshakeMessage message = new HandshakeMessage(peerID);
			
			// Add to the executor pool.

		
		
		} catch (UnknownHostException e) {
			System.out.println("Unknown host: hostName");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("No I/O");
			System.exit(1);
		}

		System.out.println("Type Message (\"Bye.\" to quit)");

	}
}