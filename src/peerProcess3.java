import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;

import edu.ufl.cise.client.OptimisticUnchokeTask;
import edu.ufl.cise.client.Peer;
import edu.ufl.cise.client.ScheduleNeighborTimerTask;
import edu.ufl.cise.config.MetaInfo;
import edu.ufl.cise.config.PeerInfo;
import edu.ufl.cise.util.CommonConfigReader;
import edu.ufl.cise.util.PeerConfigReader;

public class peerProcess3 {

	public static final String COMMON_CONFIG = "Common.cfg";
	public static final String PEER_CONFIG = "PeerInfo.cfg";

	static String base_path;
	static String peerID;

	public static String getBasePath() {
		return base_path;
	}

	public static void main(String args[]) throws IOException {
		// String peerId = args[0];
		String peerId = "1";
		peerID = peerId;
		base_path = System.getProperty("user.home") + "/";
		// System.out.println(base_path);
		init(peerId);
	}

	public static void init(String peerId) throws IOException {
		String directory = "project" + peerId + "/";
		// Reads the Common Config file
		CommonConfigReader.configReader(COMMON_CONFIG, base_path + directory);

		// Sets the number of pieces
		int fileSize = MetaInfo.getFileSize();
		int pieceSize = MetaInfo.getPieceSize();
		int nPieces = (int) Math.ceil((float) fileSize / (float) pieceSize);
		int lastPieceSize = fileSize -  (fileSize/pieceSize)*pieceSize;
		
		MetaInfo.setLastPieceSize(lastPieceSize);
		MetaInfo.setnPieces(nPieces);
		MetaInfo.setBasePath(base_path);

		// Create a log file and set the path
		String logFile = base_path + directory + "peer_" + peerID + ".log";
		MetaInfo.setLogPath(logFile);
		createLogFile();

		// Set peerID
		MetaInfo.setPeerId(Integer.parseInt(peerId));

		// Reads the peerConfig file
		LinkedHashMap<Integer, PeerInfo> peerMap;
		peerMap = PeerConfigReader.configReader(PEER_CONFIG, base_path
				+ directory, Integer.parseInt(peerId));

		System.out.println(MetaInfo.getString());
		
		// Initializes PeerClient
/*		int peerIdInt = Integer.parseInt(peerId);
		Peer.getInstance().init(peerIdInt, peerMap);

		// Set isCompleteFlag
		boolean isCompleteFile = Peer.getInstance().getMap().get(peerId)
				.isCompleteFile();
		MetaInfo.setCompletefile(isCompleteFile);

		// Starts PeerServer
		Peer.getInstance().Serverinit();

		// Starts PeerClient
		Peer.getInstance().clientInit();

		// Start neighbor scheduler task thread
		ScheduleNeighborTimerTask.initTimerTast();

		// Start optimistically unchoke scheduler task thread
		OptimisticUnchokeTask.initTimerTast();
*/
	}

	private static void createLogFile() {
		String fileName = MetaInfo.getLogPath();
		File file = new File(fileName);
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
