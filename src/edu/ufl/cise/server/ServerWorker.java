package edu.ufl.cise.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Date;

import edu.ufl.cise.client.Peer;
import edu.ufl.cise.config.MetaInfo;
import edu.ufl.cise.protocol.BitTorrentProtocol;
import edu.ufl.cise.protocol.HandshakeMessage;
import edu.ufl.cise.protocol.Message;
import edu.ufl.cise.protocol.ReadWorker;
import edu.ufl.cise.protocol.Message.MessageType;
import edu.ufl.cise.util.ExecutorPool;
import edu.ufl.cise.util.Logger;

public class ServerWorker extends ReadWorker implements Runnable {
	private Socket clientSocket = null;
	boolean LISTENING = true;
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
		
		// update isConnected map
		Peer.getInstance().getIsConnected().put(peerID, true);
		
		String logMessage = "Peer " + this.currPeerId + " is connected from Peer "  + this.peerID;
		//Logger.getInstance().log(logMessage);
		Logger.log(logMessage);
	}

	public void run() {
		int bytesRead = -1;
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
			while (LISTENING) {
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
					//System.out.println("Read bytes should be 14: " + bytesRead);
					header = getHeader(firstFour, temp);
					String headerString = new String(header);

					if (headerString.equalsIgnoreCase(HandshakeMessage.HEADER)) {
						//System.out.println("Header");
						//bytesRead = in.read(temp, 18, 14); // read the remaining bytes
						for( int i=18; i<32; i++){
							temp[i] = (byte) in.read();
						}
						//System.out.println("Read bytes should be 14: " + bytesRead);
						int peerId = getPeerId(temp);
						assert(peerID == peerId); // verify the peer id
						//String logMessage = "Peer " + MetaInfo.getPeerId() + " received the handshake message from Peer " 
						//		+ peerId ;
						//Logger.getInstance().log(logMessage);
						//Logger.log(logMessage);

						//System.out.println("Received peerID: " + peerId);
						response = new HandshakeMessage(peerId);
						response.setmType(MessageType.HANDSHAKE);
						//System.out.println(response.mType);
					}
				} else {// Determine the message type and construct it
					//System.out.println("Second type of message");
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
			
				// Create a BitTorrent protocol job and pass it to executor service.
				BitTorrentProtocol protocol = new BitTorrentProtocol(response, peerID);
				ExecutorPool.getInstance().getPool().execute(protocol);
			}
		} catch (Exception e) {
			// Add a log statement
			//Date date = new Date();
			//System.out.println(date.getTime() + " : " + peerID);
			//System.out.println(" bytesRead : " + bytesRead);
			//e.printStackTrace();
			LISTENING = false;
			//System.out.println("Shutting down Server worker");
			//Peer.getInstance().shutdown();
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