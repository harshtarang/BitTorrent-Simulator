package edu.ufl.cise.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Socket;

import edu.ufl.cise.config.MetaInfo;
import edu.ufl.cise.protocol.BitField;
import edu.ufl.cise.protocol.BitTorrentProtocol;
import edu.ufl.cise.protocol.Choke;
import edu.ufl.cise.protocol.HandshakeMessage;
import edu.ufl.cise.protocol.Have;
import edu.ufl.cise.protocol.Interested;
import edu.ufl.cise.protocol.Message;
import edu.ufl.cise.protocol.NotInterested;
import edu.ufl.cise.protocol.Piece;
import edu.ufl.cise.protocol.ReadWorker;
import edu.ufl.cise.protocol.Request;
import edu.ufl.cise.protocol.SendMessage;
import edu.ufl.cise.protocol.Unchoke;
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
			Peer.getInstance().updateSocket(peerID, clientSocket);
			
			// Send Handshake message
			HandshakeMessage handShakeMessage = new  HandshakeMessage(currPeerId);
			SendMessage message = new SendMessage(peerID, handShakeMessage.getBytes());
			ExecutorPool.getInstance().getPool().execute(message);
			
			// Update the status of handshake sent
			Peer.getInstance().updateHandshakeSent(peerID);

			// Now just wait for replies from the peer
			byte[] firstFour = new byte[4];
			while (true) {
				// Determine message type and create a message
				Message response = null;
				in.read(firstFour, 0, 4);
				// Check the type of message
				if (isHandShakeMessage(firstFour)) {
					byte[] temp = new byte[14];
					byte[] header;
					in.read(temp, 4, 14);  // read next 14 
					header = getHeader(firstFour, temp);
					String headerString = new String(header);
					if (headerString.equalsIgnoreCase(HandshakeMessage.HEADER)) {
						temp = new byte[10];
						in.read(temp, 18, 10);  // read the next 10 bytes which should be zero so ignore them
						byte[] peer = new byte[4];
						in.read(peer, 28, 4);   // read the next 4 which is peerId
						int peerId = new BigInteger(peer).intValue();
						response = new HandshakeMessage(peerId);
						// process handshake message.
						// verify the handhshake
						// Need to store socket information in the map
					}
				} else {// Determine the message type and construct it
					int len = new BigInteger(firstFour).intValue();  // get the length of message
					byte[] temp = new byte[len];
					in.read(temp, 4, len);
					response = returnMessageType(len, temp);
				}
				// Create a BitTorrent protocol job and pass it to executor service.
				BitTorrentProtocol protocol = new BitTorrentProtocol(response, peerID);
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