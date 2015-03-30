package edu.ufl.cise.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

import sun.security.util.BigInt;
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

public class ClientWorker implements Runnable {

	Socket clientSocket;
	OutputStream out;
	InputStream in;
	int peerID;
	int port;
	String hostName;

	public ClientWorker(int peerId, int port, String hostName) {
		this.peerID = peerId;
		this.port = port;
		this.hostName = hostName;
	}

	public void run() {
		try {
			clientSocket = new Socket(hostName, port);
			out = clientSocket.getOutputStream();
			in = clientSocket.getInputStream();
			// Create a handshake message. 
			HandshakeMessage message = new HandshakeMessage(peerID);
			
			// Add to the executor pool.

		
		
		} catch (UnknownHostException e) {
			System.out.println("Unknown host: hostName");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("No I/O");
			System.exit(1);
		}

		System.out.println("Type Message (\"Bye.\" to quit)");

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