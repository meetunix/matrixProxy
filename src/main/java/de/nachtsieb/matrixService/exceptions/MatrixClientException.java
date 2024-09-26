package de.nachtsieb.matrixService.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * This exception build an HTTP response with 500 status (internal server error) and an optional
 * message in the body. It is thrown due to errors in the matrix client module.
 */
public class MatrixClientException extends WebApplicationException {

  public MatrixClientException() {
    super(Response.noContent().status(Response.Status.INTERNAL_SERVER_ERROR).build());
  }

  public MatrixClientException(String msg) {
    super(
        Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(msg)
            .type("text/plain")
            .build());
  }
}
