package edu.ufl.cise.util;

import java.io.BufferedReader;
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
	
	public static void configReader( String fileName, String filePath){
		BufferedReader br = null;
		try {
			String line;
			br = new BufferedReader(new FileReader(filePath + fileName));
			while ((line = br.readLine()) != null) {
				if(line.startsWith("#") || line.isEmpty()) continue;
				
				String arr[] = line.split(" ");
				String property = arr[0];
				String value = arr[1];
				
				if( property.equalsIgnoreCase(NumberOfPreferredNeighbors)){
					int numNeighbours = Integer.parseInt(value);
					MetaInfo.setNumPreferredNeighbours(numNeighbours);
					
				}else if( property.equalsIgnoreCase(OptimisticUnchokingInterval)){
					int optimistUnchokingInterval = Integer.parseInt(value);
					MetaInfo.setOptimisticUnchokingInterval(optimistUnchokingInterval);
					
				}else if( property.equalsIgnoreCase(UnchokingInterval)){
					int unchokingInterval = Integer.parseInt(value);
					MetaInfo.setUnchokingInterval(unchokingInterval);
					
				}else if( property.equalsIgnoreCase(FileName)){
					MetaInfo.setFileName(value);
				}else if( property.equalsIgnoreCase(FileSize)){
					int fileSize = Integer.parseInt(value);
					MetaInfo.setFileSize(fileSize);
				}else if( property.equalsIgnoreCase(PieceSize)){
					int pieceSize = Integer.parseInt(value);
					MetaInfo.setPieceSize(pieceSize);
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
	}
}
