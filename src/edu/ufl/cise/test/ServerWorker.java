package edu.ufl.cise.test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.Socket;

import edu.ufl.cise.protocol.Choke;
import edu.ufl.cise.protocol.HandshakeMessage;
import edu.ufl.cise.protocol.Have;
import edu.ufl.cise.protocol.Message;
import edu.ufl.cise.protocol.Request;

public class ServerWorker extends ReadWorker implements Runnable {

	private Socket clientSocket = null;
	private DataOutputStream out;
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
			out = new DataOutputStream(clientSocket.getOutputStream());
			in = clientSocket.getInputStream();
			PeerInfo.getInstance().updateOutputStream(peerID, out);
			PeerInfo.getInstance().updateSocket(peerID, clientSocket);

			// Send Handshake message
			HandshakeMessage handShakeMessage = new HandshakeMessage(currPeerId);
			SendMessage message = new SendMessage(peerID,
					handShakeMessage.getBytes());
			ExecutorPool.getInstance().getPool().execute(message);

			// Send Have message
			Have haveMessage = new Have(12);
			message = new SendMessage(peerID,
					haveMessage.getBytes());
			ExecutorPool.getInstance().getPool().execute(message);

			// Send Request message
			Request requestMessage = new Request(12);
			message = new SendMessage(peerID,
					requestMessage.getBytes());
			ExecutorPool.getInstance().getPool().execute(message);
			
			// Send Choke message
			Choke chokeMessage = new Choke();
			message = new SendMessage(peerID,
					chokeMessage.getBytes());
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