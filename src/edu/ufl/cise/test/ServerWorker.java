package edu.ufl.cise.test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;

import edu.ufl.cise.protocol.Choke;
import edu.ufl.cise.protocol.HandshakeMessage;
import edu.ufl.cise.protocol.Have;
import edu.ufl.cise.protocol.Interested;
import edu.ufl.cise.protocol.Message;
import edu.ufl.cise.protocol.NotInterested;
import edu.ufl.cise.protocol.Request;
import edu.ufl.cise.protocol.Unchoke;

public class ServerWorker extends ReadWorker implements Runnable {

	private Socket clientSocket = null;
	private OutputStream out;
	private InputStream in;
	int peerID;
	int port;
	int currPeerId;

	public ServerWorker(Socket socket) {
		System.out.println("Received request");
		this.clientSocket = socket;
		String hostname = clientSocket.getInetAddress().getCanonicalHostName();
		this.peerID = PeerInfo.getInstance().getHostNameToIdMap().get(hostname);
		this.port = PeerInfo.getInstance().getPortNumber();
		this.currPeerId = PeerInfo.getInstance().getPeerId();
	}

	public void run() {
		try {
			out = clientSocket.getOutputStream();
			in = clientSocket.getInputStream();
			PeerInfo.getInstance().updateOutputStream(peerID, out);
			PeerInfo.getInstance().updateSocket(peerID, clientSocket);

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
					System.out.println("* Handshake message Received *");
					temp = new byte[32];
					in.read(temp, 4, 14);  // read next 14 
					header = getHeader(firstFour, temp);
					String headerString = new String(header);
					
					if (headerString.equalsIgnoreCase(HandshakeMessage.HEADER)) {
						System.out.println("* Header Verified *");
						in.read(temp, 18, 14);  // read the remaining bytes
						int peerId = getPeerId(temp);  
						System.out.println("Received peerID: " + peerId);
						response = new HandshakeMessage(peerId);
					}
				} else {  // Determine the message type and construct it
					System.out.println("* Second type of Message *");
					int len = new BigInteger(firstFour).intValue();  // get the length of message
					System.out.println("Length of message is : " + len);
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