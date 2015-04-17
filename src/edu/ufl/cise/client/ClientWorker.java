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
import edu.ufl.cise.protocol.Message.MessageType;
import edu.ufl.cise.protocol.ReadWorker;
import edu.ufl.cise.protocol.SendMessage;
import edu.ufl.cise.util.ExecutorPool;
import edu.ufl.cise.util.Logger;

public class ClientWorker extends ReadWorker implements Runnable {

	Socket clientSocket;
	OutputStream out;
	InputStream in;
	int currPeerId;
	int peerID;
	int port;
	String hostName;

	public ClientWorker(int peerId, int port, String hostName) {
		System.out.println("Starting clientWorker for: " + peerId);
		this.currPeerId = MetaInfo.getPeerId();
		this.peerID = peerId;
		this.port = port;
		this.hostName = hostName;
	}

	public void run() {
		try {
			// connect to the socket
			// TODO : Fix the hardcoding before running on the cise machines. 
			clientSocket = new Socket("localhost", port);

			// update isConnected map
			Peer.getInstance().getIsConnected().put(peerID, true);
			
			String logMessage = "Peer " + this.currPeerId + " makes a connection to Peer "  + this.peerID;
			Logger.getInstance().log(logMessage);

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
				//System.out.println(" port number:"+port);
				in.read(firstFour, 0, 4);
				if (isHandShakeMessage(firstFour)) { // Check the type of
														// message
					System.out.println("Handshake message");
					temp = new byte[32];
					in.read(temp, 4, 14); // read next 14
					header = getHeader(firstFour, temp);
					String headerString = new String(header);

					if (headerString.equalsIgnoreCase(HandshakeMessage.HEADER)) {
						//System.out.println("Header");
						in.read(temp, 18, 14); // read the remaining bytes
						int peerId = getPeerId(temp);
						 logMessage = "Peer " + MetaInfo.getPeerId() + " received the handshake message from Peer " 
								+ peerId ;
						Logger.getInstance().log(logMessage);
						//System.out.println("Received peerID: " + peerId);
						response = new HandshakeMessage(peerId);
						response.setmType(MessageType.HANDSHAKE);
					}
				} else {// Determine the message type and construct it
					//System.out.println("Message type 2");
					int len = new BigInteger(firstFour).intValue(); // get the
																	// length of
																	// message
					temp = new byte[len + 4];
					in.read(temp, 4, len);
					response = returnMessageType(len, temp,peerID);
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