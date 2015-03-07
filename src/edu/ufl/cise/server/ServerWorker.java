package edu.ufl.cise.server;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import edu.ufl.cise.config.MetaInfo;

public class ServerWorker extends Thread {
	private MetaInfo metaInfo;
	private Socket clientSocket = null;
	private PrintWriter out;
	private BufferedReader in;

	public ServerWorker(Socket socket) {
		this.clientSocket = socket;
	}

	public void run() {
		try {
			try {
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));
				System.out.println("Request from server to handle "
						+ clientSocket);
			} catch (EOFException e) {
				// Add a log statement here
				System.out.println("Something Went wrong with the connection.");
			}

			out.println("HI");
			String inputLine;
			String outputLine;
			while ((inputLine = in.readLine()) != null) {
				System.out.println("ServerWorker: Received input " + inputLine);
				//outputLine = inputLine;
				out.println("ServerBye");
				if (inputLine.equals("ClientBye"))
					break;
			}
			clientSocket.close();
		} catch (IOException e) {
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