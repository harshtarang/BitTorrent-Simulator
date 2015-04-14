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

public class OptimisticUnchokeTask extends TimerTask {

	private static int time;

	@Override
	public void run() {
		optimisticallyUnchokeNeighbor();
	}

	private void optimisticallyUnchokeNeighbor() {
		try {
			// Invoke optimistically unchoke strategy
			randomSelect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void randomSelect() {
		Random random = new Random();
		ArrayList<Integer> interestedPeerList = new ArrayList<Integer>();
		HashMap<Integer, Boolean> currentlyInterested = Peer.getInstance()
				.getCurrentlyInterested();
		HashMap<Integer, Boolean> currentPreferredNeigbor = Peer.getInstance()
				.getPreferredNeighbors();
		int selectedOUN = -1;
		int count = 0;
		
		// Get the currently Interested neighbors
		Iterator<Integer> itr1 = currentlyInterested.keySet().iterator();
		while (itr1.hasNext()) {
			int peerId = itr1.next();
			if (currentlyInterested.get(peerId))
				interestedPeerList.add(peerId);
		}
		int currentInterestedSize = interestedPeerList.size();
		
		if (currentInterestedSize > 0) {
			while (count++ < currentInterestedSize) { // to break the possibly
														// infinite loop when
														// interested ones are
														// same as preferred
														// neighbors
				Integer randNum = random.nextInt(currentInterestedSize);
				int peerId = interestedPeerList.get(randNum);
				if (currentlyInterested.get(peerId)
						&& currentInterestedSize == 1) { // if only one is
															// interested then
															// just take it
					selectedOUN = peerId;
					break;
				} else if (currentPreferredNeigbor.get(peerId)) { // more than 1
																	// interested
																	// but
																	// exists in
																	// preferred
																	// neighbor
					selectedOUN = peerId;
					continue;
				}
			}
		} else { // if no one is interested just return
			return;
		}
		
		// Send choke or unchoke message
		int previouseOUN = Peer.getInstance().getCurrentOptimisticUnchoked();
		if (previouseOUN != selectedOUN) {
			// Send choke message to previous OUN if its not in current selected
			// neighbor
			if (!Peer.getInstance().getPreferredNeighbors().get(previouseOUN)) {
				Choke choke = new Choke();
				SendMessage sendMessage = new SendMessage(previouseOUN,
						choke.getBytes());
				ExecutorPool.getInstance().getPool().execute(sendMessage);
			}
			// Set the current OUN
			Peer.getInstance().setCurrentOptimisticUnchoked(selectedOUN);
			// Send the unchoke message if its not already unchoked
			if(!Peer.getInstance().getPreferredNeighbors().get(selectedOUN)){
				Unchoke unchoke = new Unchoke();
				SendMessage sendMessage = new SendMessage(selectedOUN,
						unchoke.getBytes());
				ExecutorPool.getInstance().getPool().execute(sendMessage);
			}
		}
	}

	public static void initTimerTast() {
		time = MetaInfo.getOptimisticUnchokingInterval();
		TimerTask timerTask = new OptimisticUnchokeTask();
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(timerTask, 0, time*1000);
	}

}