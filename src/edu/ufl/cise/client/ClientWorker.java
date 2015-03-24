package edu.ufl.cise.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientWorker implements Runnable {

	Socket clientSocket;
	PrintWriter out;
	BufferedReader in;
	int port;
	String hostName;

	public ClientWorker(int port, String hostName) {
		this.port = port;
		this.hostName = hostName;
	}

	public void run() {
		try {
			clientSocket = new Socket(hostName, port);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.out.println("Unknown host: hostName");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("No I/O");
			System.exit(1);
		}

		// Send the first message
		out.println("HELLO");

		String userInput;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(
				System.in));
		System.out.println("Type Message (\"Bye.\" to quit)");
		try {
			while ((userInput = stdIn.readLine()) != null) {
				out.println(userInput);
				// end loop
				if (userInput.equals("Bye."))
					break;
				// System.out.println("echo: " + in.readLine());
			}
			out.println(System.in);
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