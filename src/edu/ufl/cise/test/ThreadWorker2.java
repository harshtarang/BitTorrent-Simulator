package edu.ufl.cise.test;

public class ThreadWorker2 implements Runnable {

	int threadNum;

	public ThreadWorker2(int thread) {
		this.threadNum = thread;
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(100);
				int num = SomeClass.getInstance().getValue();
				System.out.println("Doing something with value " + threadNum);
				num++;
				SomeClass.getInstance().setValue(num);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
