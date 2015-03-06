package edu.ufl.cise.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import edu.ufl.cise.config.MetaInfo;

public class CommonConfigReader {

	public static final String NumberOfPreferredNeighbors = "NumberOfPreferredNeighbors";
	public static final String UnchokingInterval = "UnchokingInterval";
	public static final String OptimisticUnchokingInterval = "OptimisticUnchokingInterval";
	public static final String FileName = "FileName";
	public static final String FileSize = "FileSize";
	public static final String PieceSize = "PieceSize";
	
	public static MetaInfo configReader( File fileName, String filePath){
		BufferedReader br = null;
		MetaInfo metaInfo = new MetaInfo();
		try {
			String line;
			br = new BufferedReader(new FileReader(filePath + fileName));
			while ((line = br.readLine()) != null) {
				String arr[] = line.split(" ");
				String property = arr[0];
				String value = arr[1];
				if( property.equalsIgnoreCase(NumberOfPreferredNeighbors)){
					int numNeighbours = Integer.parseInt(value);
					metaInfo.setNumPreferredNeighbours(numNeighbours);
				}else if( property.equalsIgnoreCase(OptimisticUnchokingInterval)){
					int optimistUnchokingInterval = Integer.parseInt(value);
					metaInfo.setOptimisticUnchokingInterval(optimistUnchokingInterval);
				}else if( property.equalsIgnoreCase(UnchokingInterval)){
					int unchokingInterval = Integer.parseInt(value);
					metaInfo.setUnchokingInterval(unchokingInterval);
				}else if( property.equalsIgnoreCase(FileName)){
					metaInfo.setFileName(value);
				}else if( property.equalsIgnoreCase(FileSize)){
					int fileSize = Integer.parseInt(value);
					metaInfo.setFileSize(fileSize);
				}else if( property.equalsIgnoreCase(PieceSize)){
					int pieceSize = Integer.parseInt(value);
					metaInfo.setPieceSize(pieceSize);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return metaInfo;
	}
}
