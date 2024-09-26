package de.nachtsieb.matrixService;

import java.util.EnumMap;
import javax.inject.Singleton;

@Singleton
public class MatrixProxyConfig {

  private final EnumMap<Configuration, String> config;

  MatrixProxyConfig(EnumMap<Configuration, String> config) {
    this.config = config;
  }

  public String getHomeserver() {
    return config.get(Configuration.HOMESERVER_URL);
  }

  public String getLogin() {
    return config.get(Configuration.HOMESERVER_USER);
  }


  public String getPassword() {
    return config.get(Configuration.HOMESERVER_PASS);
  }

}
