import java.io.IOException;
import java.util.LinkedHashMap;

import edu.ufl.cise.client.OptimisticUnchokeTask;
import edu.ufl.cise.client.Peer;
import edu.ufl.cise.client.ScheduleNeighborTimerTask;
import edu.ufl.cise.config.MetaInfo;
import edu.ufl.cise.config.PeerInfo;
import edu.ufl.cise.util.CommonConfigReader;
import edu.ufl.cise.util.PeerConfigReader;


public class peerProcess2 {

	public static final String COMMON_CONFIG = "Commong.cfg";
	public static final String PEER_CONFIG   = "PeerInfo.cfg";
	
	static String base_path;
	
	public static String getBasePath(){
		// Get base path of project
		base_path = "";
		return base_path;
	}
	
	public static void main(String args[]) throws IOException{
		//String peerId = args[0];
		String peerId = "2";
		init(peerId);
	}
	
	public static void init(String peerId) throws IOException{
		// Get the base project path
		
		// Initializes peer directory.
		createDirectory();
		
		// Reads the Common Config file
		CommonConfigReader.configReader(COMMON_CONFIG, base_path);

		// Sets the number of pieces
		int fileSize = MetaInfo.getFileSize();
		int pieceSize = MetaInfo.getPieceSize();
		int nPieces = (int) Math.ceil( (float)fileSize/ (float)pieceSize) ;
		MetaInfo.setnPieces(nPieces);
		
		// Set peerID 
		MetaInfo.setPeerId(Integer.parseInt(peerId));
		
		// Reads the peerConfig file
		LinkedHashMap<Integer, PeerInfo> peerMap; 
		peerMap = PeerConfigReader.configReader(PEER_CONFIG, base_path, Integer.parseInt(peerId));
		
		// Initializes PeerClient
		int peerIdInt = Integer.parseInt(peerId);
		Peer.getInstance().init(peerIdInt, peerMap);
		
		// Set isCompleteFlag
		boolean isCompleteFile = Peer.getInstance().getMap().get(peerId).isCompleteFile();
		MetaInfo.setCompletefile(isCompleteFile);
		
		// Starts PeerServer
		Peer.getInstance().Serverinit();
		
		// Starts PeerClient
		Peer.getInstance().clientInit();
		
		// Start neighbor scheduler task thread
		ScheduleNeighborTimerTask.initTimerTast();

		// Start optimistically unchoke scheduler task thread
		OptimisticUnchokeTask.initTimerTast();
	}

	private static void createDirectory() {
		
	}
	
}
