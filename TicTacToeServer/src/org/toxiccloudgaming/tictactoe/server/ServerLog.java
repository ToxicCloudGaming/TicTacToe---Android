package org.toxiccloudgaming.tictactoe.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerLog {
	
	public static final String LOG_INFO = "?INFO?";
	public static final String LOG_WARNING = "!WARNING!";
	public static final String LOG_ERROR = "!!!ERROR!!!";
	
	private static final String LOG_TIME_FORMAT = "yyyy/MM/dd hh:mm:ss";
	
	private static final String LOG_FILE = "/var/lib/tomcat7/webapps/ROOT/logs/tictactoe_log.txt";
	
	private FileWriter writer;
	
	public FileWriter getWriter() {
		try {
			if(this.writer != null) {
				return this.writer;
			} else {
				File logFile = new File(LOG_FILE);
				if(logFile.exists()) {
					return new FileWriter(logFile, true);
				} else {
					return new FileWriter(logFile);
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void close() {
		try {
			this.writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void log(String text, String level) {
		String logString = this.getTimeTag() + " (" + level + "): " + text + "\n";
		PrintWriter out = new PrintWriter(new BufferedWriter(this.getWriter()));
		out.print(logString);
		out.flush();
    }
	
	public void logException(Exception e) {
		StringWriter stackTrace = new StringWriter();
		e.printStackTrace(new PrintWriter(stackTrace));
		this.log(stackTrace.toString(), ServerLog.LOG_ERROR);
	}
	
	public String getTimeTag() {
		DateFormat dateFormat = new SimpleDateFormat(LOG_TIME_FORMAT);
		Date date = new Date();
		return dateFormat.format(date);
	}
}
