package edu.ufl.cise.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Socket;

import edu.ufl.cise.client.Peer;
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
import edu.ufl.cise.protocol.Unchoke;
import edu.ufl.cise.util.ExecutorPool;

public class ServerWorker extends ReadWorker implements Runnable {
	private Socket clientSocket = null;
	private OutputStream out;
	private InputStream in;
	int peerID;

	public ServerWorker(Socket socket) {
		this.clientSocket = socket;
		String hostname = clientSocket.getInetAddress().getCanonicalHostName();
		this.peerID = MetaInfo.getHostNameToIdMap().get(hostname);
		Peer.getInstance().getMap().get(peerID).setSocket(socket);
	}

	public void run() {
		try {
			out = clientSocket.getOutputStream();
			in = clientSocket.getInputStream();
			// byte[] buffer = new byte[1024];
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
						// Need to store socket information in the map
						Peer.getInstance().updateSocket(peerId, clientSocket);
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