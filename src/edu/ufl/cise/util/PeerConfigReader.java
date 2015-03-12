package edu.ufl.cise.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import edu.ufl.cise.config.PeerInfo;

public class PeerConfigReader {
	

	public static ArrayList<PeerInfo> configReader( String fileName, String filePath){
		BufferedReader br = null;
		ArrayList<PeerInfo> peerMap = new ArrayList<PeerInfo>();
		try {
			String line;
			br = new BufferedReader(new FileReader(filePath + fileName));
			while ((line = br.readLine()) != null) {
				PeerInfo peerInfo = new PeerInfo();
				String arr[] = line.split(" ");
				String peerID = arr[0];
				String hostName = arr[1];
				int port = Integer.parseInt(arr[2]);
				boolean isCompleteFile = Boolean.parseBoolean(arr[3]);
				peerInfo = new PeerInfo(peerID, hostName, port, isCompleteFile);
				peerMap.add(peerInfo);
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
		return peerMap;
	}
}
