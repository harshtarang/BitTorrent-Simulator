package edu.ufl.cise.test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Socket;

import edu.ufl.cise.protocol.BitField;
import edu.ufl.cise.protocol.Choke;
import edu.ufl.cise.protocol.HandshakeMessage;
import edu.ufl.cise.protocol.Have;
import edu.ufl.cise.protocol.Interested;
import edu.ufl.cise.protocol.Message;
import edu.ufl.cise.protocol.NotInterested;
import edu.ufl.cise.protocol.Piece;
import edu.ufl.cise.protocol.Request;
import edu.ufl.cise.protocol.Unchoke;

public class ClientWorker extends ReadWorker implements Runnable {

	Socket clientSocket;
	OutputStream out;
	InputStream in;
	int currPeerId;
	int peerID;
	int port;
	String hostName;

	public ClientWorker(int peerId, int port, String hostName) {
		this.currPeerId = PeerInfo.getInstance().getPeerId();  
		this.peerID = peerId;
		this.port = port;
		this.hostName = hostName;
		System.out.println("Connecting to " + peerID + "  " + port);
	}

	public void run() {
		try {
			clientSocket = new Socket(hostName, port);
			System.out.println(" Created socket " + hostName + " " + port);
			
			out = clientSocket.getOutputStream();
			in = clientSocket.getInputStream();
			PeerInfo.getInstance().updateSocket(peerID, clientSocket);
			PeerInfo.getInstance().updateOutputStream(peerID, out);
			
			// Send Handshake message
			HandshakeMessage handShakeMessage = new HandshakeMessage(12);
			SendMessage message = new SendMessage(peerID,
					handShakeMessage.getBytes());
			ExecutorPool.getInstance().getPool().execute(message);

			// Send Interested message
			Interested haveMessage = new Interested();
			message = new SendMessage(peerID,
					haveMessage.getBytes());
			ExecutorPool.getInstance().getPool().execute(message);

			// Send NI message
			NotInterested chokeMessage = new NotInterested();
			message = new SendMessage(peerID,
					chokeMessage.getBytes());
			ExecutorPool.getInstance().getPool().execute(message);

			// Send Unchoke message
			Unchoke unchokeMessage = new Unchoke();
			message = new SendMessage(peerID,
					unchokeMessage.getBytes());
			ExecutorPool.getInstance().getPool().execute(message);

			
			// Now just wait for replies from the peer
			byte[] firstFour = new byte[4];
			byte[] temp;
			byte[] header;
			Message response = null;

			while (true) {
				in.read(firstFour, 0, 4);
				if (isHandShakeMessage(firstFour)) {	// Check the type of message
					System.out.println("Handshake message");
					temp = new byte[32];
					in.read(temp, 4, 14);  // read next 14 
					header = getHeader(firstFour, temp);
					String headerString = new String(header);
					
					if (headerString.equalsIgnoreCase(HandshakeMessage.HEADER)) {
						System.out.println("Header");
						in.read(temp, 18, 14);  // read the remaining bytes
						int peerId = getPeerId(temp);  
						System.out.println("Received peerID: " + peerId);
						response = new HandshakeMessage(peerId);
					}
				} else {// Determine the message type and construct it
					System.out.println("Message type 2");
					int len = new BigInteger(firstFour).intValue();  // get the length of message
					temp = new byte[len+4];
					in.read(temp, 4, len);
					response = returnMessageType(len, temp);
				}
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
}