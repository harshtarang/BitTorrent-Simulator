package edu.ufl.cise.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import edu.ufl.cise.config.MetaInfo;

public class Logger {

	// Ensures a single instance for the logger
	//private static volatile Logger instance;
	private static String fileName;
	private static OutputStream out = null;

	public  Logger() {
		fileName = MetaInfo.getLogPath();
		try {
			out = new FileOutputStream(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

/*	public static Logger getInstance() {
		if (instance == null) {
			instance = new Logger();
		}
		return instance;
	}
*/
	public static void close() {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	public static synchronized void log(String message) {
		try {
			Date date = new Date();
			String logMessage = date.toString() + ":  " + message + "\n";
			out.write(logMessage.getBytes());
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}
}
