package edu.ufl.cise.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import edu.ufl.cise.config.MetaInfo;
import edu.ufl.cise.config.PeerInfo;

public class PeerConfigReader {
	

	public static LinkedHashMap<Integer, PeerInfo> configReader( String fileName, String filePath, int peerId){
		BufferedReader br = null;
		LinkedHashMap<Integer, PeerInfo> peerMap = new LinkedHashMap<Integer, PeerInfo>();
		HashMap<String, Integer> hostNameToPeerIdMap = new HashMap<String, Integer>();
		ArrayList<Integer> peerList = new ArrayList<Integer>();
		int numPieces = MetaInfo.getnPieces();
		try {
			String line;
			int countPeersComplete = 0;
			br = new BufferedReader(new FileReader(filePath + fileName));
			while ((line = br.readLine()) != null) {
				if(line.startsWith("#") || line.isEmpty()) continue;
				
				PeerInfo peerInfo = new PeerInfo();
				String arr[] = line.split(" ");

				Integer peerIDInt = Integer.parseInt(arr[0]);
				peerList.add(peerIDInt);
				String hostName = arr[1];
				int port = Integer.parseInt(arr[2]);
				boolean isCompleteFile = Boolean.parseBoolean(arr[3]);
				
				peerInfo = new PeerInfo(peerIDInt, hostName, port, isCompleteFile);
				if(isCompleteFile){
					countPeersComplete += 1;
					peerInfo.setNumPiecesInterested(0);
				}
				else{
					peerInfo.setNumPiecesInterested(numPieces);
				}
				
				peerMap.put(peerIDInt, peerInfo);
				hostNameToPeerIdMap.put(hostName, peerIDInt);
				
				if(peerId == peerIDInt.intValue()){
					MetaInfo.setPortNumber(port);
				}
			}
			MetaInfo.setHostNameToIdMap(hostNameToPeerIdMap);
			// Set number of peers in MetaInfo
			MetaInfo.setNumPeers(peerMap.size());
			// Set peerList in MetaInfo
			MetaInfo.setPeerList(peerList);
			// initialize peers complete
			MetaInfo.setPeersComplete(countPeersComplete);
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
