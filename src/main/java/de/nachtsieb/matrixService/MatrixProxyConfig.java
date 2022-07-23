package de.nachtsieb.matrixService;

import javax.inject.Singleton;

@Singleton
public class MatrixProxyConfig {

  private String homeserver;
  private String login;
  private String password;

  MatrixProxyConfig(String homeserver, String login, String password) {
    this.setHomeserver(homeserver);
    this.setLogin(login);
    this.setPassword(password);
  }

  public String getHomeserver() {
    return homeserver;
  }

  private void setHomeserver(String homeserver) {
    this.homeserver = homeserver;
  }

  public String getLogin() {
    return login;
  }

  private void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  private void setPassword(String password) {
    this.password = password;
  }
}
