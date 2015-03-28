package edu.ufl.cise.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import sun.security.util.BigInt;
import edu.ufl.cise.client.Peer;
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

public class ServerWorker implements Runnable {
	private Socket clientSocket = null;
	private OutputStream out;
	private InputStream in;

	public ServerWorker(Socket socket) {
		this.clientSocket = socket;
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
					if (headerString.equalsIgnoreCase(Message.HEADER)) {
						temp = new byte[10];
						in.read(temp, 18, 10);  // read the next 10 bytes which should be zero so ignore them
						byte[] peer = new byte[4];
						in.read(peer, 28, 4);   // read the next 4 which is peerId
						int peerId = new BigInt(peer).toInt();
						response = new HandshakeMessage(peerId);
						// process handshake message.
						// Need to store socket information in the map
						Peer.getInstance().updateClientSocket(peerId, clientSocket);
					}
				} else {// Determine the message type and construct it
					int len = new BigInt(firstFour).toInt();  // get the length of message
					byte[] temp = new byte[len];
					in.read(temp, 4, len);
					response = returnMessageType(len, temp);
				}
				// Create a BitTorrent protocol job and pass it to executor service.
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

	private byte[] getHeader(byte[] firstFour, byte[] temp) {
		byte[] header = new byte[18];
		for(int i=0; i<4; i++){
			header[i] = firstFour[i];
		}
		for(int i=0; i<14; i++){
			header[4+i] = temp[i];
		}
		return header;
	}

	public boolean isHandShakeMessage(byte[] firstFour) {
		String firstFourBytes = new String(firstFour);
		if (isNumeric(firstFourBytes)) {
			return false;
		} else
			return true;
	}

	public Message returnMessageType(int len, byte[] input) {
		// Determine message type
		byte[] mType = new byte[1];
		mType[0] = input[0];
		int messageType = -1;
		try {
			messageType = Integer.parseInt(new String(mType, "UTF-8"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(1);
		}
		int pos = 1;

		Message response = null;
		if (messageType == Message.MessageType.CHOKE.getValue()) {
			response = new Choke();
		} else if (messageType == Message.MessageType.UNCHOKE.getValue()) {
			response = new Unchoke();
		} else if (messageType == Message.MessageType.INTERESTED.getValue()) {
			response = new Interested();
		} else if (messageType == Message.MessageType.NOT_INTERESTED.getValue()) {
			response = new NotInterested();
		} else if (messageType == Message.MessageType.HAVE.getValue()) {
			byte[] pieceIndex = new byte[4];
			pieceIndex = getPieceIndex(pos, input);
			response = new Have(pieceIndex);
		} else if (messageType == Message.MessageType.BITFIELD.getValue()) {
			byte[] bitArray = new byte[len-1];
			bitArray = getBitArray(input, pos, len-1);
			response = new BitField(bitArray);
		} else if (messageType == Message.MessageType.REQUEST.getValue()) {
			byte[] pieceIndex = new byte[4];
			pieceIndex = getPieceIndex(pos, input);
			response = new Request(pieceIndex);
		} else if (messageType == Message.MessageType.PIECE.getValue()) {
			byte[] pieceIndex = new byte[4];
			pieceIndex = getPieceIndex(pos, input);
			int index = new BigInt(pieceIndex).toInt();
			len--;
			pos += 4;
			byte[] piece = getBitArray(input, pos, len);
			response = new Piece(index, piece);
		}
		return response;
	}

	private byte[] getPieceIndex(int pos, byte[] input){
		byte[] pieceIndex = new byte[4];
		pieceIndex[0] = input[pos]; pieceIndex[1] = input[pos+1];
		pieceIndex[2] = input[pos+2]; pieceIndex[3] = input[pos+3];
		return pieceIndex;
	}
	
	private byte[] getBitArray(byte[] input, int pos, int len) {
		byte[] bitArray = new byte[len];
		for (int i = 0; i < len; i++) {
			bitArray[i] = input[pos + i];
		}
		return bitArray;
	}

	private boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

}