package edu.ufl.cise.config;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

	// Ensures a single instance for the logger
	private static Logger instance = null;

	private Logger() {
	}

	public static Logger getInstance() {
		if (instance == null) {
			instance = new Logger();
		}
		return instance;
	}

	public void log(String peerId, String message) {
		String fileName = "log_peer_" + peerId;
		BufferedWriter bw = null;
		try {
			try {
				bw = new BufferedWriter(new FileWriter(fileName));
				bw.write(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			try {
				if (bw != null)
					bw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
