package edu.ufl.cise.client;

import java.util.Timer;
import java.util.TimerTask;

import edu.ufl.cise.config.MetaInfo;

public class ScheduleNeighborTimerTask extends TimerTask {

	private static int time;
	private static boolean firstTime;
	static TimerTask timerTask;
	static Timer timer;

	@Override
	public void run() {
		if( Peer.getInstance().getNumPeersCompleted() == MetaInfo.getNumPeers()){
			System.out.println("Pers completeed : SchedulePN");
			timerTask.cancel();
			timer.cancel();
			return; 
		}
		determineNeigbor();
		//System.out.println("Peers completed: " + Peer.getInstance().getNumPeersCompleted());
	}

	private void determineNeigbor() {
		System.out.println("determine PN");
		boolean isCompleteFile = MetaInfo.isCompletefile();
		try {
			if (isCompleteFile) {
				Peer.getInstance().randomSelect();
			} else if (firstTime) {
				firstTime = false;
				Peer.getInstance().randomSelect();
			} else {
				// downloadRatePrioritySelect();
				Peer.getInstance().randomSelect();
			}
			// Invoke optimistically unchoke strategy
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void downloadRatePrioritySelect() {
	}

	public static void initTimerTast() {
		firstTime = true;
		time = MetaInfo.getUnchokingInterval();
		timerTask = new ScheduleNeighborTimerTask();
		timer = new Timer(true);
		timer.scheduleAtFixedRate(timerTask, 15000, time*1000);
	}

}