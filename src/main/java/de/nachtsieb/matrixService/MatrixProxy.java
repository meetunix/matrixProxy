package de.nachtsieb.matrixService;

import de.nachtsieb.logging.MatrixLogger;
import de.nachtsieb.matrixService.types.MessageQueue;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Callable;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/*
 * Copyright © 2020 Martin Steinbach
 *
 * See file LICENSE for license information
 *
 */

@Command(
    description = "A tiny Matrix proxy for sending simple text messages to a room",
    mixinStandardHelpOptions = true,
    name = "matrixProxy",
    version = "matrixProxy 0.3.0")
public class MatrixProxy implements Callable<Integer> {

  @SuppressWarnings("FieldMayBeFinal")
  @Option(
      names = {"-v", "--verbose"},
      description = "Be more verbose")
  private boolean verbose = false;

  @SuppressWarnings("FieldMayBeFinal")
  @Option(
      names = {"-c", "--conf"},
      required = true,
      description = "full path to the config file")
  private String confFilePath = null;

  @SuppressWarnings("FieldMayBeFinal")
  @Option(
      names = {"-d", "--database"},
      required = true,
      description = "full path to the persistent database directory")
  private String databaseFilePath = null;

  public static String baseURI;
  private static MatrixProxyConfig conf;
  private static MessageQueue messageQueue;

  /**
   * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
   *
   * @return Grizzly HTTP server.
   */
  public static HttpServer startServer() {
    // create a resource config that scans for JAX-RS resources and providers
    // in de.nachtsieb.matrixService package
    final ResourceConfig rc = new ResourceConfig().packages("de.nachtsieb.matrixService");
    rc.property(ServerProperties.WADL_FEATURE_DISABLE, true);
    // rc.property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE,true);

    messageQueue = new MessageQueue();

    // inject an instance of the class MatrixProxyConfig to the application
    rc.register(
        new AbstractBinder() {
          @Override
          protected void configure() {
            bind(conf).to(MatrixProxyConfig.class);
            bind(messageQueue).to(MessageQueue.class);
          }
        });

    // create and start a new instance of grizzly http server
    // exposing the Jersey application at baseURI
    return GrizzlyHttpServerFactory.createHttpServer(URI.create(baseURI), rc);
  }

  @Override
  public Integer call() {

    MatrixLogger.initiate(verbose);
    loadConfFile(confFilePath);
    HttpServer server = null;
    try {
      server = startServer();
      while (Thread.currentThread().isAlive()) Thread.sleep(1000);
    } catch (Exception e) {
      Objects.requireNonNull(server).shutdownNow();
      e.printStackTrace();
      return 1;
    }
    return 0;
  }

  private void loadConfFile(String confFilePath) {

    Properties props = new Properties();

    try {

      Path path = Paths.get(confFilePath);
      InputStream is = Files.newInputStream(path, StandardOpenOption.READ);
      props.load(is);

      baseURI = props.getProperty("BASE_URL");
      String homeserver = props.getProperty("HOMESERVER_URL");
      String login = props.getProperty("HOMESERVER_USER");
      String password = props.getProperty("HOMESERVER_PASS");

      conf = new MatrixProxyConfig(homeserver, login, password);

      MatrixLogger.info(
          "using config file "
              + confFilePath
              + "\nbase URI: "
              + baseURI
              + "\nhomeserver: "
              + homeserver
              + "\nlogin: "
              + login
              + "\n");

    } catch (InvalidPathException | IOException e) {
      MatrixLogger.severe("unable to load config file from given path " + confFilePath);
      MatrixLogger.severe(e.toString());
      System.exit(1);
    }
  }

  public static void main(String[] args) {
    int exitCode = new CommandLine(new MatrixProxy()).execute(args);
    System.exit(exitCode);
  }
}
