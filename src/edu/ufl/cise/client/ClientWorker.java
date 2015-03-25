package edu.ufl.cise.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.ufl.cise.config.MetaInfo;
import edu.ufl.cise.protocol.BitTorrentProtocol;

public class ClientWorker implements Runnable {

	Socket clientSocket;
	OutputStream out;
	InputStream in;
	int port;
	String hostName;

	public ClientWorker(int port, String hostName) {
		this.port = port;
		this.hostName = hostName;
		// Create a handshake message
		// Create a job for sending it.
		// Add to the executor pool.
		// 
	}

	public void run() {
		try {
			clientSocket = new Socket(hostName, port);
			out = clientSocket.getOutputStream();
			in = clientSocket.getInputStream();
			int pieceLength = MetaInfo.getPieceSize();
			byte[] piece = new byte[pieceLength];
			int offset = 0;
			while (true) {
				int flag = in.read(piece, offset, pieceLength);
				if (flag == -1) {
					// Create a job and send to executor service
					//BitTorrentProtocol.processInput(piece);
					piece = new byte[pieceLength];
				} else {
					// clientSocket.close();
				}
			}
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