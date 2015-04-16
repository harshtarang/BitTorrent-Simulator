package edu.ufl.cise.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import edu.ufl.cise.config.MetaInfo;

public class Logger {

	// Ensures a single instance for the logger
	private static volatile Logger instance;
	private static String fileName;
	private static BufferedWriter bw = null;

	private Logger() {
		fileName = MetaInfo.getLogPath();
	}

	public static Logger getInstance() {
		if (instance == null) {
			synchronized (Logger.class) {
				if (instance == null)
					instance = new Logger();
			}
		}
		return instance;
	}

	public static void close() {
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	public void log(String message) {
		try {
			Date date = new Date();
			bw = new BufferedWriter(new FileWriter(fileName));
			bw.write(date.getTime() + ": " + message);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
