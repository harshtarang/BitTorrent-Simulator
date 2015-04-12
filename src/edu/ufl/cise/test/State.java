package edu.ufl.cise.test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

	class State{
		int port;
		Socket socket;
		OutputStream out;
		boolean firstMessageSent;
		boolean firstMessageReceived;
		boolean secondMessageSent;
		boolean secondMessageReceived;

		public State(int port){
			this.port = port;
			this.firstMessageSent = false;
			this.firstMessageReceived = false;
			this.secondMessageSent = false;
			this.secondMessageReceived = false;
		}
		
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}
		public Socket getSocket() {
			return socket;
		}
		public void setSocket(Socket socket) {
			this.socket = socket;
		}
		public boolean isFirstMessageSent() {
			return firstMessageSent;
		}
		public void setFirstMessageSent(boolean firstMessageSent) {
			this.firstMessageSent = firstMessageSent;
		}
		public boolean isFirstMessageReceived() {
			return firstMessageReceived;
		}
		public void setFirstMessageReceived(boolean firstMessageReceived) {
			this.firstMessageReceived = firstMessageReceived;
		}
		public boolean isSecondMessageSent() {
			return secondMessageSent;
		}
		public void setSecondMessageSent(boolean secondMessageSent) {
			this.secondMessageSent = secondMessageSent;
		}
		public boolean isSecondMessageReceived() {
			return secondMessageReceived;
		}
		public void setSecondMessageReceived(boolean secondMessageReceived) {
			this.secondMessageReceived = secondMessageReceived;
		}

		public OutputStream getOut() {
			return out;
		}

		public void setOut(OutputStream out) throws IOException {
			this.out = out;
			System.out.println("  INDISE STATE  " );
		}
	}
