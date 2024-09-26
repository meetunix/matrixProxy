package de.nachtsieb.matrixService.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MatrixLogin {

  String type;
  String user;
  String password;

  public MatrixLogin() {}

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
