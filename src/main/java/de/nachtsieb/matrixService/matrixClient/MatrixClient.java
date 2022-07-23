package de.nachtsieb.matrixService.matrixClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.nachtsieb.matrixService.exceptions.MatrixClientException;

public interface MatrixClient {

  /** Sends a message to a previously performed login. */
  void sendMessage(String message) throws JsonProcessingException, MatrixClientException;

  /** Performs a logout out for the current used session. */
  void logout() throws MatrixClientException;
}
