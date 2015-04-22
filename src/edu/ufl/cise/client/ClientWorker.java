package edu.ufl.cise.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Date;

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

	boolean LISTENING = true;
	Socket clientSocket;
	OutputStream out;
	InputStream in;
	int currPeerId;
	int peerID;
	int port;
	String hostName;

	public ClientWorker(int peerId, int port, String hostName) {
		//System.out.println("Starting clientWorker for: " + peerId);
		this.currPeerId = MetaInfo.getPeerId();
		this.peerID = peerId;
		this.port = port;
		this.hostName = hostName;
	}

	public void run() {
		int bytesRead = -1;
		byte[] temp = null;
		try {
			// connect to the socket
			// TODO : Fix the hardcoding before running on the cise machines. 
			clientSocket = new Socket("localhost", port);
			//clientSocket = new Socket(hostName, port);
			
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
			byte[] header;
			Message response = null;

			while (LISTENING) {
				//System.out.println(" port number:"+port);
				//bytesRead = in.read(firstFour, 0, 4);
				for( int i=0; i<4; i++){
					//bytesRead = in.read(firstFour, 0, 4);
					firstFour[i] = (byte) in.read();
				}
				//System.out.println("Read bytes should be 4: " + bytesRead);
				if (isHandShakeMessage(firstFour)) { // Check the type of
														// message
					//System.out.println("Handshake message");
					temp = new byte[32];
					//bytesRead = in.read(temp, 4, 14); // read next 14
					for( int i=4; i<18; i++){
						temp[i] = (byte) in.read();
					}
					//System.out.println("Read bytes should be 14 : " + bytesRead);
					header = getHeader(firstFour, temp);
					String headerString = new String(header);

					if (headerString.equalsIgnoreCase(HandshakeMessage.HEADER)) {
						//System.out.println("Header");
						//bytesRead = in.read(temp, 18, 14); // read the remaining bytes
						for( int i=18; i<32; i++){
							temp[i] = (byte) in.read();
						}
						//System.out.println("Read bytes shold be 14:  " + bytesRead);
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
					//bytesRead = in.read(temp, 4, len);
					for( int i=4; i<len+4; i++ ){
						temp[i] = (byte)in.read();
					}
					//System.out.println("Read bytes should be " + len + " : " + bytesRead);
					response = returnMessageType(len, temp,peerID);
				}
				// Create a BitTorrent protocol job and pass it to executor
				// service.
				BitTorrentProtocol protocol = new BitTorrentProtocol(response,
						peerID);
				ExecutorPool.getInstance().getPool().execute(protocol);
			}
		} catch (Exception e) {
			Date date = new Date();
			//System.out.println(date.getTime() + " : " + peerID);
			//System.out.println("bytesRead : " + bytesRead);
			//System.out.println("bytes: " + temp);
			//e.printStackTrace();
			System.out.println("Client Worker");
			LISTENING = false;
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
			} catch (IOException ex) {
				//ex.printStackTrace();
			}

		}
	}
}