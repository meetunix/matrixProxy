package de.nachtsieb.matrixService.entities;

import javax.xml.bind.annotation.XmlRootElement;

/** Represents the message sent to the Matrix homeserver. */
@XmlRootElement
public class MatrixMessage {

  String body;
  String formatted_body;
  final String format = "org.matrix.custom.html";
  final String msgtype = "m.text";


  public MatrixMessage() {}

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getFormatted_body() {
    return formatted_body;
  }

  public String getMsgtype() {
    return msgtype;
  }

  public String getFormat() {
    return format;
  }

  public void setFormatted_body(String formatted_body) {
    this.formatted_body = formatted_body;
  }
}
