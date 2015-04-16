package edu.ufl.cise.config;

import java.io.OutputStream;
import java.net.Socket;
import java.util.BitSet;

public class PeerInfo {

	private Integer peerId;
	private String hostname;
	private int port;
	private boolean isCompleteFile;
	boolean isHandShakeSent = false;
	boolean isHandShakeReceived = false;
	boolean isBitFieldSent = false;
	boolean isBitFieldReceived = false;
	float downloadSpeed;
	private Socket socket;
	private OutputStream out;
	private BitSet pieceInfo;
	private int numPiecesInterested;
	
	public PeerInfo(){}
	
	public PeerInfo( Integer peerId,
			String hostname,
			int port,
			boolean isCompleteFile){
		this.peerId = peerId;
		this.hostname = hostname;
		this.port = port;
		this.isCompleteFile = isCompleteFile;
		int nPieces = MetaInfo.getnPieces();
		pieceInfo = new BitSet(nPieces);
		
		// if complete file set all the bits to true
		if(isCompleteFile){
			pieceInfo.set(0, nPieces);
		}
	}
	
	public boolean isHandShakeSent() {
		return isHandShakeSent;
	}

	public void setHandShakeSent(boolean isHandShakeSent) {
		this.isHandShakeSent = isHandShakeSent;
	}

	public boolean isHandShakeReceived() {
		return isHandShakeReceived;
	}

	public void setHandShakeReceived(boolean isHandShakeReceived) {
		this.isHandShakeReceived = isHandShakeReceived;
	}

	public boolean isBitFieldSent() {
		return isBitFieldSent;
	}

	public void setBitFieldSent(boolean isBitFieldSent) {
		this.isBitFieldSent = isBitFieldSent;
	}

	public boolean isBitFieldReceived() {
		return isBitFieldReceived;
	}

	public void setBitFieldReceived(boolean isBitFieldReceived) {
		this.isBitFieldReceived = isBitFieldReceived;
	}

	public BitSet getPieceInfo() {
		return pieceInfo;
	}

	public void setPieceInfo(BitSet pieceInfo) {
		this.pieceInfo = pieceInfo;
	}

	public float getDownloadSpeed() {
		return downloadSpeed;
	}

	public void setDownloadSpeed(float downloadSpeed) {
		this.downloadSpeed = downloadSpeed;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public Integer getPeerId() {
		return peerId;
	}

	public void setPeerId(Integer peerId) {
		this.peerId = peerId;
	}
	
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public boolean isCompleteFile() {
		return isCompleteFile;
	}
	public void setCompleteFile(boolean isCompleteFile) {
		this.isCompleteFile = isCompleteFile;
	}

	public int getNumPiecesInterested() {
		return numPiecesInterested;
	}

	public void setNumPiecesInterested(int numPiecesInterested) {
		this.numPiecesInterested = numPiecesInterested;
	}

	public void updatePieceInterested() {
		int pieceId = -1;
		int piecesInterested = MetaInfo.getnPieces();
		for (pieceId = pieceInfo.nextSetBit(0); pieceId >= 0; pieceId = pieceInfo
				.nextSetBit(pieceId + 1)) {
			piecesInterested--;
		}
		numPiecesInterested = piecesInterested;
	}

	public OutputStream getOut() {
		return out;
	}

	public void setOut(OutputStream out) {
		this.out = out;
	}
	
}
