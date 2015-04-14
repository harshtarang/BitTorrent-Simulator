package edu.ufl.cise.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import edu.ufl.cise.config.MetaInfo;
import edu.ufl.cise.protocol.Choke;
import edu.ufl.cise.protocol.SendMessage;
import edu.ufl.cise.protocol.Unchoke;
import edu.ufl.cise.util.ExecutorPool;

public class ScheduleNeighborTimerTask extends TimerTask {

	private static int time;
	private static boolean firstTime;

	@Override
	public void run() {
		determineNeigbor();
	}

	private void determineNeigbor() {
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
		TimerTask timerTask = new ScheduleNeighborTimerTask();
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(timerTask, 0, time*1000);
	}

}