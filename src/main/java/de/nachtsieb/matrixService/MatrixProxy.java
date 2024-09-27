package de.nachtsieb.matrixService;

import de.nachtsieb.logging.MatrixLogger;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumMap;
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
    version = "matrixProxy 0.3.1")
public class MatrixProxy implements Callable<String> {

  @Option(
      names = {"-v", "--verbose"},
      description = "Log more than just info.")
  private boolean verbose = false;

  @Option(
      names = {"-c", "--conf"},
      description = "full path to the config file")
  private String confFilePath = null;

  public static String baseURI;
  private static MatrixProxyConfig conf;

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

    // inject an instance of the class MatrixProxyConfig to the application
    rc.register(
        new AbstractBinder() {
          @Override
          protected void configure() {
            bind(conf).to(MatrixProxyConfig.class);
          }
        });

    // create and start a new instance of grizzly http server
    // exposing the Jersey application at baseURI
    MatrixLogger.info("Starting grizzly http server on " + baseURI);
    return GrizzlyHttpServerFactory.createHttpServer(URI.create(baseURI), rc);
  }

  @Override
  public String call() {

    MatrixLogger.initiate(verbose);

    loadConfig(confFilePath);

    final HttpServer server = startServer();

    try {

      while (true) {
        try{

          Thread.sleep(1000);

        } catch (InterruptedException e) {
          System.exit(1);
        }
      }

    } catch (Exception e) {

      server.shutdownNow();
      return null;
    }
  }

  private void loadConfig(String confFilePath) {

    try {

      EnumMap<Configuration, String> config = new EnumMap<>(Configuration.class);

      Properties props = new Properties();

      if (confFilePath != null) {
        MatrixLogger.info(
            "Config file provided (has precedence over environment variables):" + confFilePath);
        Path path = Paths.get(confFilePath);
        InputStream is = Files.newInputStream(path, StandardOpenOption.READ);
        props.load(is);

        for (Configuration confEntry : Configuration.values()) {
          String confValue = props.getProperty(confEntry.name());
          if (confValue == null)
            throw new IOException(
                "Configuration value " + confEntry.name() + " not set in configuration file.");
          config.put(confEntry, confValue);
        }
      } else {
        MatrixLogger.info("No config file provided, using environment variables for configuration");
        for (Configuration confEntry : Configuration.values()) {
          String confValue = System.getenv(confEntry.name());
          if (confValue == null)
            throw new IOException("Missing environment variable " + confEntry.name() + ".");
          config.put(confEntry, confValue);
        }
      }

      baseURI = config.get(Configuration.BASE_URL);
      conf = new MatrixProxyConfig(config);

      MatrixLogger.info(
          "base URI: "
              + baseURI
              + " | homeserver: "
              + conf.getHomeserver()
              + " | login: "
              + conf.getLogin());

    } catch (InvalidPathException pathEx) {
      MatrixLogger.severe("unable to load config file from given path " + confFilePath);
    } catch (IOException ioEx) {
      MatrixLogger.severe(ioEx.toString());
      System.exit(1);
    }
  }

  public static void main(String[] args) {
    int exitCode = new CommandLine(new MatrixProxy()).execute(args);
    System.exit(exitCode);
  }
}
