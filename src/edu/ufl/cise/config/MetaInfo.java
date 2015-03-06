package edu.ufl.cise.config;

public class MetaInfo {

	int numPreferredNeighbours;
	int unchokingInterval;
	int optimisticUnchokingInterval;
	String fileName;
	int fileSize;
	int pieceSize;

	public MetaInfo(){}

	public MetaInfo(int numPreferredNeighbours,
			int unchokingInterval,
			int optimisticUnchokingInterval, 
			String fileName,
			int fileSize,
			int pieceSize){
		this.numPreferredNeighbours = numPreferredNeighbours;
		this.unchokingInterval = unchokingInterval;
		this.optimisticUnchokingInterval = optimisticUnchokingInterval;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.pieceSize = pieceSize;
	}
	
	public int getNumPreferredNeighbours() {
		return numPreferredNeighbours;
	}
	public void setNumPreferredNeighbours(int numPreferredNeighbours) {
		this.numPreferredNeighbours = numPreferredNeighbours;
	}
	public int getUnchokingInterval() {
		return unchokingInterval;
	}
	public void setUnchokingInterval(int unchokingInterval) {
		this.unchokingInterval = unchokingInterval;
	}
	public int getOptimisticUnchokingInterval() {
		return optimisticUnchokingInterval;
	}
	public void setOptimisticUnchokingInterval(int optimisticUnchokingInterval) {
		this.optimisticUnchokingInterval = optimisticUnchokingInterval;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	public int getPieceSize() {
		return pieceSize;
	}
	public void setPieceSize(int pieceSize) {
		this.pieceSize = pieceSize;
	}
	
}
