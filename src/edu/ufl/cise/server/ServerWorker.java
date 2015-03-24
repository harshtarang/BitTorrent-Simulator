package edu.ufl.cise.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerWorker extends Thread {
	private Socket clientSocket = null;
	private PrintWriter out;
	private BufferedReader in;

	public ServerWorker(Socket socket) {
		this.clientSocket = socket;
	}

	public void run() {
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			
			System.out.println("Request from server to handle " + clientSocket);
			
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
			}

			clientSocket.close();
		} catch (IOException e) {
			// Add a log statement
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}
	}
}