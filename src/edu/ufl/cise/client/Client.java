package edu.ufl.cise.client;

public class Client {

	public static void init(){
		ClientWorker worker = new ClientWorker(9090, "marvin");
		worker.run();
	}
	
	public static void main(String args[]){
		init();
	}
	
}
