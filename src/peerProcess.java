import java.util.ArrayList;
import java.util.HashMap;

import edu.ufl.cise.client.Peer;
import edu.ufl.cise.client.TimerWorker;
import edu.ufl.cise.config.MetaInfo;
import edu.ufl.cise.config.PeerInfo;
import edu.ufl.cise.util.CommonConfigReader;
import edu.ufl.cise.util.PeerConfigReader;


public class peerProcess {

	public static final String COMMON_CONFIG = "Commong.cfg";
	public static final String PEER_CONFIG   = "PeerInfo.cfg";
	
	static String base_path;
	
	public static String getBasePath(){
		// Get base path of project
		base_path = "";
		return base_path;
	}
	
	public static void main(String args[]){
		String peerId = args[0];
		init(peerId);
	}
	
	public static void init(String peerId){
		// Get the base project path
		
		// Reads the Common Config file
		MetaInfo metainfo = null;
		metainfo = CommonConfigReader.configReader(COMMON_CONFIG, base_path);
		
		// Reads the peerConfig file
		ArrayList<PeerInfo> peerList; 
		peerList = PeerConfigReader.configReader(PEER_CONFIG, base_path);
		
		// Initializes peer directories.
		
		// Initializes PeerClient
		Peer peer = new Peer(Integer.parseInt(peerId), peerList);
		
		// Starts PeerServer
		
		// Starts PeerClient
		
		// Start Timer task thread
		TimerWorker.initTimerTast(MetaInfo.getOptimisticUnchokingInterval()*1000);
		
	}
	
}
