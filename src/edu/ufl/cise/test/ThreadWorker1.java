package edu.ufl.cise.test;

import java.util.HashMap;

public class ThreadWorker1 implements Runnable {

	int threadNum;

	public ThreadWorker1(int thread) {
		this.threadNum = thread;
	}

	public void run() {
		while (true) {
			try {
				HashMap<String, String> map = SomeClass.getInstance().getMap1();
				synchronized (map) {
					System.out.println("updating map in " + threadNum);
					Thread.sleep(100000);
					System.out.println("Exiting map in " + threadNum);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
