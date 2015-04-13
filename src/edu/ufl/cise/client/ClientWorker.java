package edu.ufl.cise.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;

import edu.ufl.cise.config.MetaInfo;
import edu.ufl.cise.protocol.BitTorrentProtocol;
import edu.ufl.cise.protocol.HandshakeMessage;
import edu.ufl.cise.protocol.Message;
import edu.ufl.cise.protocol.ReadWorker;
import edu.ufl.cise.protocol.SendMessage;
import edu.ufl.cise.util.ExecutorPool;

public class ClientWorker extends ReadWorker implements Runnable {

	Socket clientSocket;
	OutputStream out;
	InputStream in;
	int currPeerId;
	int peerID;
	int port;
	String hostName;

	public ClientWorker(int peerId, int port, String hostName) {
		this.currPeerId = MetaInfo.getPeerId();
		this.peerID = peerId;
		this.port = port;
		this.hostName = hostName;
	}

	public void run() {
		try {
			// connect to the socket
			clientSocket = new Socket(hostName, port);
			out = clientSocket.getOutputStream();
			in = clientSocket.getInputStream();

			// update socket info corresponding to the peer
			Peer.getInstance().updateSocket(peerID, clientSocket, out);

			// Send Handshake message
			HandshakeMessage handShakeMessage = new HandshakeMessage(currPeerId);
			SendMessage message = new SendMessage(peerID,
					handShakeMessage.getBytes());
			ExecutorPool.getInstance().getPool().execute(message);

			// Update the status of handshake sent
			Peer.getInstance().updateHandshakeSent(peerID);

			// Now just wait for replies from the peer
			byte[] firstFour = new byte[4];
			byte[] temp;
			byte[] header;
			Message response = null;

			while (true) {
				in.read(firstFour, 0, 4);
				if (isHandShakeMessage(firstFour)) { // Check the type of
														// message
					System.out.println("Handshake message");
					temp = new byte[32];
					in.read(temp, 4, 14); // read next 14
					header = getHeader(firstFour, temp);
					String headerString = new String(header);

					if (headerString.equalsIgnoreCase(HandshakeMessage.HEADER)) {
						System.out.println("Header");
						in.read(temp, 18, 14); // read the remaining bytes
						int peerId = getPeerId(temp);
						System.out.println("Received peerID: " + peerId);
						response = new HandshakeMessage(peerId);
					}
				} else {// Determine the message type and construct it
					System.out.println("Message type 2");
					int len = new BigInteger(firstFour).intValue(); // get the
																	// length of
																	// message
					temp = new byte[len + 4];
					in.read(temp, 4, len);
					response = returnMessageType(len, temp);
				}
				// Create a BitTorrent protocol job and pass it to executor
				// service.
				BitTorrentProtocol protocol = new BitTorrentProtocol(response,
						peerID);
				ExecutorPool.getInstance().getPool().execute(protocol);
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
}