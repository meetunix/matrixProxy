package de.nachtsieb.matrixService.types;

import de.nachtsieb.matrixService.entities.Message;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageQueue extends ConcurrentLinkedQueue<Message> {

  public MessageQueue() {
    super();
    //TODO load messages from database
  }

}
