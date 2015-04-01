package edu.ufl.cise.test;

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
        	System.out.println("HELLO");
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     
    public static void initTimerTast(){
    	time = 10000;
        TimerTask timerTask = new ScheduleNeighborTimerTask();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate( timerTask, 0, time);
    }
 
}