package de.nachtsieb.matrixService.entities;

import javax.xml.bind.annotation.XmlRootElement;

/** Represents the message sent to the Matrix homeserver. */
@XmlRootElement
public class MatrixMessage {
  String msgtype;
  String body;

  public MatrixMessage() {}

  public String getMsgtype() {
    return msgtype;
  }

  public void setMsgtype(String msgtype) {
    this.msgtype = msgtype;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }
}
