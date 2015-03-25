import java.io.IOException;
import java.util.LinkedHashMap;

import edu.ufl.cise.client.OptimisticUnchokeTask;
import edu.ufl.cise.client.Peer;
import edu.ufl.cise.client.ScheduleNeighborTimerTask;
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
	
	public static void main(String args[]) throws IOException{
		String peerId = args[0];
		init(peerId);
	}
	
	public static void init(String peerId) throws IOException{
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
		
		// Start neighbor scheduler task thread
		ScheduleNeighborTimerTask.initTimerTast();

		// Start optimistically unchoke scheduler task thread
		OptimisticUnchokeTask.initTimerTast();
	}

	private static void createDirectory() {
		
	}
	
}
