import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
		CommonConfigReader.configReader(COMMON_CONFIG, base_path);
		
		// Reads the peerConfig file
		LinkedHashMap<String, PeerInfo> peerMap; 
		peerMap = PeerConfigReader.configReader(PEER_CONFIG, base_path);
		
		// Initializes peer directory.
		createDirectory();
		
		// Initializes PeerClient
		Peer peer = new Peer(Integer.parseInt(peerId), peerMap);
		
		// Starts PeerServer
		peer.Serverinit();
		
		// Starts PeerClient
		peer.clientInit();
		
		// Start Timer task thread
		TimerWorker.initTimerTast(MetaInfo.getOptimisticUnchokingInterval()*1000);
		
		// Start neighbor scheduler task thread
		
		// Start optimistically unchoke scheduler task thread
		
		
	}

	private static void createDirectory() {
		
	}
	
}
