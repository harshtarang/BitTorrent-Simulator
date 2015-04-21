package edu.ufl.cise.client;

import java.util.Timer;
import java.util.TimerTask;

import edu.ufl.cise.config.MetaInfo;

public class OptimisticUnchokeTask extends TimerTask {

	private static int time;
	static TimerTask timerTask;
	static Timer timer;

	@Override
	public void run() {
		if (Peer.getInstance().getNumPeersCompleted() == MetaInfo.getNumPeers()) {
			//System.out.println("Pers completeed : OUN");

			timerTask.cancel();
			timer.cancel();
			return;
		}

		optimisticallyUnchokeNeighbor();
	}

	private void optimisticallyUnchokeNeighbor() {
		//System.out.println("determine OUN");
		try {
			if(MetaInfo.isShutDown()) return;
			// Invoke optimistically unchoke strategy
			Peer.getInstance().randomSelectOptimisticUnchoke();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void initTimerTast() {
		time = MetaInfo.getOptimisticUnchokingInterval();
		timerTask = new OptimisticUnchokeTask();
		timer = new Timer(true);
		timer.scheduleAtFixedRate(timerTask, 16000, time * 1000);
	}

}