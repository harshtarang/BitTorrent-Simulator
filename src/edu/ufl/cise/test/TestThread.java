package edu.ufl.cise.test;

public class TestThread {

	public static void main(String args[]){
		ThreadWorker1 worker11 = new ThreadWorker1(11);
		ThreadWorker1 worker12 = new ThreadWorker1(12);
		ThreadWorker2 worker21 = new ThreadWorker2(21);
		new Thread(worker11).start();
		new Thread(worker12).start();
		new Thread(worker21).start();
		
		while(true){
			System.out.println("Value is : " + SomeClass.getInstance().getValue());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
