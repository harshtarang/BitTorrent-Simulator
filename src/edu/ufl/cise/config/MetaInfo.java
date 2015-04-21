package edu.ufl.cise.config;

import java.util.ArrayList;
import java.util.HashMap;

public class MetaInfo {

	private static int numPreferredNeighbours;
	private static int unchokingInterval;
	private static int optimisticUnchokingInterval;
	private static String fileName;
	private static int fileSize;
	private static int pieceSize;
	private static int nPieces;
	private static boolean isCompletefile;
	private static int numPeers;
	private static ArrayList<Integer> peerList;
	private static String basePath;
	private static String logPath;
	private static int lastPieceSize;
	private static int portNumber;
	private static int peerId;
	private static int peersComplete;
	private static boolean isShutDown;
	private static HashMap<String, Integer> hostNameToIdMap; // For checking
																// which

	// peerId this
	// connection request
	// belongs to
	// when a connection
	// request comes

	public static int getnPieces() {
		return nPieces;
	}

	public static void setnPieces(int nPieces) {
		MetaInfo.nPieces = nPieces;
	}

	public MetaInfo() {
	}

	public static int getNumPreferredNeighbours() {
		return numPreferredNeighbours;
	}

	public static void setNumPreferredNeighbours(int num) {
		numPreferredNeighbours = num;
	}

	public static int getUnchokingInterval() {
		return unchokingInterval;
	}

	public static void setUnchokingInterval(int interval) {
		unchokingInterval = interval;
	}

	public static int getOptimisticUnchokingInterval() {
		return optimisticUnchokingInterval;
	}

	public static void setOptimisticUnchokingInterval(int interval) {
		optimisticUnchokingInterval = interval;
	}

	public static String getFileName() {
		return fileName;
	}

	public static void setFileName(String name) {
		fileName = name;
	}

	public static int getFileSize() {
		return fileSize;
	}

	public static void setFileSize(int size) {
		fileSize = size;
	}

	public static int getPieceSize() {
		return pieceSize;
	}

	public static void setPieceSize(int size) {
		pieceSize = size;
	}

	public static boolean isCompletefile() {
		return isCompletefile;
	}

	public static void setCompletefile(boolean isCompletefile) {
		MetaInfo.isCompletefile = isCompletefile;
	}

	public static int getNumPeers() {
		return numPeers;
	}

	public static void setNumPeers(int numPeers) {
		MetaInfo.numPeers = numPeers;
	}

	public static ArrayList<Integer> getPeerList() {
		return peerList;
	}

	public static void setPeerList(ArrayList<Integer> peerList) {
		MetaInfo.peerList = peerList;
	}

	public static String getBasePath() {
		return basePath;
	}

	public static void setBasePath(String basePath) {
		MetaInfo.basePath = basePath;
	}

	public static int getLastPieceSize() {
		return lastPieceSize;
	}

	public static void setLastPieceSize(int lastPieceSize) {
		MetaInfo.lastPieceSize = lastPieceSize;
	}

	public static int getPortNumber() {
		return portNumber;
	}

	public static void setPortNumber(int portNumber) {
		MetaInfo.portNumber = portNumber;
	}

	public static int getPeerId() {
		return peerId;
	}

	public static void setPeerId(int peerId) {
		MetaInfo.peerId = peerId;
	}

	public static HashMap<String, Integer> getHostNameToIdMap() {
		return hostNameToIdMap;
	}

	public static void setHostNameToIdMap(
			HashMap<String, Integer> hostNameToIdMap) {
		MetaInfo.hostNameToIdMap = hostNameToIdMap;
	}

	public static int getPeersComplete() {
		return peersComplete;
	}

	public static void setPeersComplete(int peersComplete) {
		MetaInfo.peersComplete = peersComplete;
	}

	public static String getString() {
		StringBuilder sb = new StringBuilder();
		sb.append("numPreferredNeighbours: " + getNumPreferredNeighbours() + "\n");
		sb.append("unchokingInterval: " + getUnchokingInterval()+ "\n");
		sb.append("optimisticUnchokingInterval: " + getOptimisticUnchokingInterval()+ "\n");
		sb.append("fileName: " + getFileName()+ "\n");
		sb.append("fileSize: " + getFileSize()+ "\n");
		sb.append("pieceSize: " + getPieceSize()+ "\n");
		sb.append("nPieces: " + getnPieces()+ "\n");

		sb.append("isCompletefile: " + isCompletefile()+ "\n");
		sb.append("numPeers: " + getNumPeers()+ "\n");
		sb.append("peerList: " + getPeerList().toString()+ "\n");
		sb.append("basePath: " + getBasePath()+ "\n");
		sb.append("lastPieceSize: " + getLastPieceSize()+ "\n");
		sb.append("portNumber: " + getPortNumber()+ "\n");
		sb.append("peerId: " + getPeerId()+ "\n");
		sb.append("hostNameToIdMap: " + hostNameToIdMap.toString()+ "\n");
		sb.append("logPath: "  + getLogPath()+ "\n");
		sb.append("PeersComplete: " + getPeersComplete() + "\n");
		return sb.toString();
	}

	public static String getLogPath() {
		return logPath;
	}

	public static void setLogPath(String logPath) {
		MetaInfo.logPath = logPath;
	}

	public static boolean isShutDown() {
		return isShutDown;
	}

	public static void setShutDown(boolean isShutDown) {
		MetaInfo.isShutDown = isShutDown;
	}
}
