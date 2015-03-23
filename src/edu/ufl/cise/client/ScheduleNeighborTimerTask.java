package edu.ufl.cise.client;

import java.util.Timer;
import java.util.TimerTask;

 
public class ScheduleNeighborTimerTask extends TimerTask {
 
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
     
    public static void initTimerTast(int time){
        TimerTask timerTask = new ScheduleNeighborTimerTask();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate( timerTask, 0, time);
    }
 
}