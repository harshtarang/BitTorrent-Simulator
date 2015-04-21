package edu.ufl.cise.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorPool {
	
	public static int NUM_THREADS = 10;
	private ExecutorService pool;
	private static volatile ExecutorPool instance;
		
	public synchronized static ExecutorPool getInstance() {
		if (instance == null) {
			 instance = new ExecutorPool();
			
		}
		return instance;
	}
	
	private ExecutorPool() {
        try {
            pool = Executors.newFixedThreadPool(NUM_THREADS);
        }
        catch ( Exception e ) {
            //e.printStackTrace();
        	System.out.println("Shutting down executor pool");
        }
    }

	public ExecutorService getPool() {
		return pool;
	}

	public void setPool(ExecutorService pool) {
		this.pool = pool;
	}
}
