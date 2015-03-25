package edu.ufl.cise.client;

import java.util.Timer;
import java.util.TimerTask;

import edu.ufl.cise.config.MetaInfo;

 
public class ScheduleNeighborTimerTask extends TimerTask {
 
	private static int time;
	
    @Override
    public void run() {
    	determineNeigbor();
    }
 
    private void determineNeigbor() {
        try{
        	// Invoke optimistically unchoke strategy
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     
    public static void initTimerTast(){
    	time = MetaInfo.getUnchokingInterval();
        TimerTask timerTask = new ScheduleNeighborTimerTask();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate( timerTask, 0, time);
    }
 
}