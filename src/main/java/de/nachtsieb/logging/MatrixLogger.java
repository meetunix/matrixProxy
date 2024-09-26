package de.nachtsieb.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class MatrixLogger {

  private static final Logger log = Logger.getLogger(MatrixLogger.class.getCanonicalName());

  public static void initiate(boolean verbose) {

    System.out.println(log.getHandlers().length);

    for (Handler h :log.getHandlers()) {
      System.out.println(h);
    }

    if (verbose) log.setLevel(Level.FINER);
    else log.setLevel(Level.INFO);

    System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s [%1$tc]: - %5$s%n");

    Handler consoleHandler = new ConsoleHandler();
    consoleHandler.setLevel(Level.ALL);
    consoleHandler.setFormatter(new SimpleFormatter());
    log.addHandler(consoleHandler);
    log.setUseParentHandlers(false);

    log.info("Logger successfully started with level: " + log.getLevel());
  }

  public static void severe(String message) {
    log.severe(message);
  }

  public static void warn(String message) {
    log.warning(message);
  }

  public static void info(String message) {
    log.info(message);
  }

  public static void config(String message) {
    log.config(message);
  }

  public static void finer(String message) {
    log.finer(message);
  }
}
