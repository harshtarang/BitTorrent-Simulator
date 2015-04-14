package edu.ufl.cise.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;

import edu.ufl.cise.client.Peer;
import edu.ufl.cise.config.MetaInfo;
import edu.ufl.cise.protocol.BitTorrentProtocol;
import edu.ufl.cise.protocol.HandshakeMessage;
import edu.ufl.cise.protocol.Message;
import edu.ufl.cise.protocol.ReadWorker;
import edu.ufl.cise.util.ExecutorPool;

public class ServerWorker extends ReadWorker implements Runnable {
	private Socket clientSocket = null;
	private OutputStream out;
	private InputStream in;
	int peerID;
	int currPeerId;

	public ServerWorker(Socket socket) {
		this.clientSocket = socket;
		String hostName = clientSocket.getInetAddress().getCanonicalHostName();
		// Currently for testing purposes since everything is localhost we will append 
		// count to hostname and extract the peerId. in fact count = current peerId.
		// TODO : Fix it before running on cise machines.
		int count = Peer.getInstance().getCount();
		hostName = hostName + count;
		this.peerID = MetaInfo.getHostNameToIdMap().get(hostName);
		this.currPeerId = MetaInfo.getPeerId();
	}

	public void run() {
		try {
			out = clientSocket.getOutputStream();
			in = clientSocket.getInputStream();
			// update socket info corresponding to the peer
			Peer.getInstance().updateSocket(peerID, clientSocket, out);

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
					System.out.println("Second type of message");
					int len = new BigInteger(firstFour).intValue(); // get the
																	// length of
																	// message
					temp = new byte[len + 4];
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