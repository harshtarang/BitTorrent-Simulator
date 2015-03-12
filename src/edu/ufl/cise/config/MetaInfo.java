package edu.ufl.cise.config;

public class MetaInfo {

	private static int numPreferredNeighbours;
	private static int unchokingInterval;
	private static int optimisticUnchokingInterval;
	private static String fileName;
	private static int fileSize;
    private static int pieceSize;

	public MetaInfo(){}

	public MetaInfo(int numPreferredNeighbours,
			int unchokingInterval,
			int optimisticUnchokingInterval, 
			String fileName,
			int fileSize,
			int pieceSize)
	{
		numPreferredNeighbours = numPreferredNeighbours;
		unchokingInterval = unchokingInterval;
		optimisticUnchokingInterval = optimisticUnchokingInterval;
		fileName = fileName;
		fileSize = fileSize;
		pieceSize = pieceSize;
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
	
}
