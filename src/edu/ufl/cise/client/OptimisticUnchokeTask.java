package edu.ufl.cise.client;

import java.util.Timer;
import java.util.TimerTask;

import edu.ufl.cise.config.MetaInfo;

 
public class OptimisticUnchokeTask extends TimerTask {
 
    @Override
    public void run() {
    	optimisticallyUnchokeNeighbor();
    }
 
    private void optimisticallyUnchokeNeighbor() {
        try{
        	// Invoke optimistically unchoke strategy
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     
    public static void initTimerTast(){
    	int time = MetaInfo.getOptimisticUnchokingInterval();
        TimerTask timerTask = new OptimisticUnchokeTask();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate( timerTask, 0, time);
    }
 
}