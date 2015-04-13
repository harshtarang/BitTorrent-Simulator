package edu.ufl.cise.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import edu.ufl.cise.config.MetaInfo;

public class Logger {

	// Ensures a single instance for the logger
	private static volatile Logger instance ;
	private static String fileName;

	private Logger() {
		fileName = MetaInfo.getLogPath();
	}

	public static Logger getInstance() {
		if (instance == null) {
			synchronized(Logger.class){
				if(instance == null) instance = new Logger();
			}
		}
		return instance;
	}

	public void log(String peerId, String message) {
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
