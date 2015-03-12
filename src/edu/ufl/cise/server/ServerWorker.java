package edu.ufl.cise.server;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import edu.ufl.cise.config.MetaInfo;
import edu.ufl.cise.protocol.BitTorrentProtocol;

public class ServerWorker extends Thread {
	private MetaInfo metaInfo;
	private Socket clientSocket = null;
	private OutputStream out;
	private InputStream in;

	public ServerWorker(Socket socket) {
		this.clientSocket = socket;
	}

	public void run() {
		try {
			out = clientSocket.getOutputStream();
			in = clientSocket.getInputStream();
			String respose = BitTorrentProtocol.receiveStream(in);
			System.out.println("Request from server to handle " + clientSocket);
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