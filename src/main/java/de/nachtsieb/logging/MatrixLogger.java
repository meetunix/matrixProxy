package de.nachtsieb.logging;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public abstract class MatrixLogger {
	
	private static final Logger log = Logger.getLogger(MatrixLogger.class.getCanonicalName());
	public static final String DEFAULT_BASE_PATH = "%t";
	public static final String DEFAULT_LOG_FILE_NAME = "/MatrixProxy%g.log";
	public static final int DEFAULT_MAX_FILE_SIZE = 10485760; // byte -> 10 MiB
	public static final int DEFAULT_MAX_FILES = 3; 
	
	
	public static void initiate() {
		initiate(DEFAULT_BASE_PATH, DEFAULT_MAX_FILE_SIZE, DEFAULT_MAX_FILES);
	}

	public static void initiate(String logPath, int maxFileSize) {
		initiate(logPath, maxFileSize, DEFAULT_MAX_FILES);
	}

	public static void initiate(String logPath) {
		initiate(logPath, DEFAULT_MAX_FILE_SIZE, DEFAULT_MAX_FILES);
	}

	
	/**
	 * 
	 * Set up the logging system. Has to be called first.
	 * 
	 * Allowed ranges:
	 * 
	 * maxFileSize:	[102400,134217728] 100 KiB to 128 MiB
	 * maxFiles:	[1,1024]
	 * 
	 * @param logPath		The base path to the logging files
	 * @param maxFileSize	The maximum size in bytes a log file is able to grow
	 * @param maxFiles		The maximum amount of log files in logPath
	 */
	public static void initiate(String logPath, int maxFileSize, int maxFiles ) {

		
		log.setLevel(Level.FINER);
		
		// a intuitive log format: "LEVEL [TIME]: (SOURCE) - MESSAGE"
		System.setProperty(
		//		"java.util.logging.SimpleFormatter.format","%4$s [%1$tc]: (%2$s) - %5$s%n");
				"java.util.logging.SimpleFormatter.format","%4$s [%1$tc]: - %5$s%n");
			
		// the name of the log file is static
		logPath = logPath + DEFAULT_LOG_FILE_NAME;
		
		// use default value if  max file size is less than 100 KiB or greater than 128 MiB
		if ( maxFileSize < 102400 || maxFileSize > 134217728 ) {
			maxFileSize = DEFAULT_MAX_FILE_SIZE;
			log.config("maximum file size out of range, using default value: "
					+ DEFAULT_MAX_FILE_SIZE);
		}
		// do not allow 0 or less log files and not more than 1024
		if (maxFiles < 1 || maxFiles > 1024) {
			maxFiles = DEFAULT_MAX_FILES; 
			log.config("maximum amount of log files out of range, using default value: "
					+ DEFAULT_MAX_FILES);
		}
		
		try {

			Handler handler = new FileHandler(logPath, maxFileSize, maxFiles);
			handler.setLevel(Level.FINER);
			handler.setFormatter(new SimpleFormatter());
			log.addHandler(handler);

			log.info(String.format("%s\n%-16s: %s\n%-16s: %s\n%-16s: %s\n%-16s: %s\n",
						"file logger successfull started with:",
						"LOG_LEVEL", log.getLevel(),
						"FH_LEVEL", handler.getLevel(),
						"MAX_FILE_SIZE", maxFileSize,
						"MAX_FILES", maxFiles					
					));
			
		} catch (IOException | SecurityException e) {
			
			Handler h = new ConsoleHandler();
			h.setLevel(Level.FINER);
			log.addHandler(h);

			MatrixLogger.finer("log files not created due to following excpetion issues");
			MatrixLogger.finer("using console logger with LEVEL " + h.getLevel());
			MatrixLogger.finer(e.toString());
		}
		
	}
	
	/**
	 * Writes a log message with level SEVERE
	 * 
	 * @param message
	 */
	public static void severe(String message) {
		log.severe(message);
	}
	
	/**
	 * Writes a log message with level WARNING
	 * 
	 * @param message
	 */
	public static void warn(String message) {
		log.warning(message);
	}

	/**
	 * Writes a log message with level INFO
	 * 
	 * @param message
	 */
	public static void info(String message) {
		log.info(message);
	}

	/**
	 *  Writes an message with log level CONFIG
	 * 
	 * @param message
	 */
	public static void config(String message) {
		log.config(message);
	}
	
	/**
	 *  Writes an message with log level FINER
	 * 
	 * @param message
	 */
	public static void finer(String message) {
		log.finer(message);
	}

	

}
