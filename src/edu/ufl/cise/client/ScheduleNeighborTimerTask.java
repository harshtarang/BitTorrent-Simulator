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
				randomSelect();
			} else if (firstTime) {
				firstTime = false;
				randomSelect();
			} else {
				// downloadRatePrioritySelect();
				randomSelect();
			}
			// Invoke optimistically unchoke strategy
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void downloadRatePrioritySelect() {
	}

	private void randomSelect() {
		int k = MetaInfo.getNumPreferredNeighbours();
		ArrayList<Integer> peerList = new ArrayList<Integer>();
		HashMap<Integer, Boolean> currentlyInterested = Peer.getInstance().getCurrentlyInterested();
		// Get the currently Interested neighbors
		Iterator<Integer> itr1 = currentlyInterested.keySet().iterator();
		while(itr1.hasNext()){
			int peerId = itr1.next();
			if(currentlyInterested.get(peerId)) peerList.add(peerId);
		}
		int currentInterestedSize = currentlyInterested.size();
		Random random = new Random();
		int count = 0;
		HashMap<Integer, Boolean> newlySelectedNeighbor = new HashMap<Integer, Boolean>();
		if (currentInterestedSize > k) {
			while (count < k) {
				Integer randNum = random.nextInt(k);
				int peerId = peerList.get(randNum);
				if (!newlySelectedNeighbor.containsKey(peerId)) {
					newlySelectedNeighbor.put(peerId, true);
					count++;
				}
			}
		}
		else{ // Add everyone
			for(int peerId: peerList) newlySelectedNeighbor.put(peerId, true);
		}
		// Iterate the current map and send choke and unchoke messages based on newly selected map.
		HashMap<Integer, Boolean> oldMap = Peer.getInstance().getPreferredNeighbors();
		Iterator<Integer> itr = oldMap.keySet().iterator();
		while(itr.hasNext()){
			int peerID = itr.next();
			if ( oldMap.get(peerID) && newlySelectedNeighbor.containsKey(peerID)){ // if an already preferred neighbor is selected again
				continue;                                                          // keep it and enjoy. No need to send unchoke message
			}
			else if( oldMap.get(peerID) && !newlySelectedNeighbor.containsKey(peerID)){
				// Peer was a preferred neighbor but not selected. Send choke message
				Choke choke = new Choke();
				SendMessage sendMessage = new SendMessage(peerID, choke.getBytes());
				ExecutorPool.getInstance().getPool().execute(sendMessage);
				// Add the current peer entry as not interested
				newlySelectedNeighbor.put(peerID, false);
			}
			else if( !oldMap.get(peerID) && newlySelectedNeighbor.containsKey(peerID)){
				// Peer was not a preferred neighbor but now selected. Send unchoke message
				Unchoke unchoke = new Unchoke();
				SendMessage sendMessage = new SendMessage(peerID, unchoke.getBytes());
				ExecutorPool.getInstance().getPool().execute(sendMessage);
			}else{ // neither it was a preferred neighbor nor it got selected
				// Just add an entry in the current map
				newlySelectedNeighbor.put(peerID, false);
			}
		}
		// Finally update the current preferred neighbor map in Peer
		Peer.getInstance().setPreferredNeighbors(newlySelectedNeighbor);
	}

	public static void initTimerTast() {
		firstTime = true;
		time = MetaInfo.getUnchokingInterval();
		TimerTask timerTask = new ScheduleNeighborTimerTask();
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(timerTask, 0, time);
	}

}