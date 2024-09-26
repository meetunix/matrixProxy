package de.nachtsieb.matrixService.matrixClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.nachtsieb.logging.MatrixLogger;
import de.nachtsieb.matrixService.entities.MatrixLogin;
import de.nachtsieb.matrixService.entities.MatrixMessage;
import de.nachtsieb.matrixService.exceptions.MatrixClientException;
import de.nachtsieb.matrixService.restClient.RestClient;
import de.nachtsieb.matrixService.restClient.RestClientJersey;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Simple wrapper class for the Matrix client server api. Handles:
 *
 * <p>* login via password * join a room if not already present * send a message to the room *
 * logout
 *
 * @author Martin Steinbach
 */

/* used api endpoints with example
 *
 * /_matrix/client/r0/joined_rooms -> lists all joined rooms (GET)
 * 		https://localhost:8448/_matrix/client/r0/joined_rooms?access_token=
 *
 * /_matrix/client/r0/join/{roomIdOrAlias} (POST) -->
 * 		https://localhost:8448/_matrix/client/r0/join/%23klingel%3Alocalhost?access_token=
 *
 * /_matrix/client/r0/rooms/{roomId}/send/{eventType}/{txnId} (PUT)
 * 		https://localhost:8448/_matrix/client/r0/rooms/!ljXeIjSnUvuZzcPcLO%3Alocalhost/send/m.room.message/35?a
 *
 * /_matrix/client/r0/logout
 * 		https://localhost:8448/_matrix/client/r0/logout?access_token=
 */

public class MatrixClientImpl implements MatrixClient {

  private final String BASE_API = "/_matrix/client/r0";

  private final String user;
  private final String pass;
  private final String room;
  private final String homeserver;

  private final String accessToken;
  private final String roomID;

  private final RestClient rest;

  private final ObjectMapper mapper;

  public MatrixClientImpl(String user, String pass, String room, String homeserver)
      throws JsonProcessingException, URISyntaxException, MatrixClientException {

    this.user = user;
    this.pass = pass;
    this.room = room;
    this.homeserver = homeserver;

    this.rest = new RestClientJersey();
    this.mapper = new ObjectMapper();

    this.accessToken = loginAndGetAccessToken();
    this.roomID = joinRoomAndGetRoomID();
  }

  /**
   * This method performs a login to the homeserver and returns the access token.
   *
   * @return the access token for further communication
   */
  private String loginAndGetAccessToken() throws JsonProcessingException, MatrixClientException {

    String requestURL = homeserver + BASE_API + "/login";

    MatrixLogin login = new MatrixLogin();
    login.setUser(user);
    login.setPassword(pass);
    login.setType("m.login.password");
    String jsonRequest = mapper.writeValueAsString(login);

    JsonNode node = rest.doRequest(requestURL, jsonRequest, RestClient.HTTP_METHOD_POST);

    if (node == null) {
      String errMsg = "unable to login to homeserver";
      MatrixLogger.severe(errMsg);
      throw new MatrixClientException(errMsg);
    } else {
      return node.get("access_token").asText();
    }
  }

  /**
   * This method tries to joins the current user to the previous given room.
   *
   * @return the room id sned by the server
   */
  private String joinRoomAndGetRoomID() throws URISyntaxException, MatrixClientException {

    URI serverURI = new URI(homeserver);

    String roomURL = room + ":" + serverURI.getHost();
    roomURL = URLEncoder.encode(roomURL, StandardCharsets.UTF_8);

    String requestURL = homeserver + BASE_API + "/join/" + roomURL + "?access_token=" + accessToken;
    JsonNode node = rest.doRequest(requestURL, "{}", RestClient.HTTP_METHOD_POST);

    if (node == null) {
      throw new MatrixClientException("unable to join the room " + room);
    } else {
      return node.get("room_id").asText();
    }
  }

  @Override
  public void sendMessage(String message) throws JsonProcessingException, MatrixClientException {

    String requestURL =
        homeserver
            + BASE_API
            + "/rooms/"
            + roomID
            + "/send/m.room.message/"
            + getRandomInteger(1, 9999)
            + "?access_token="
            + accessToken;

    MatrixMessage mxMessage = new MatrixMessage();
    mxMessage.setBody(message);
    mxMessage.setMsgtype("m.text");

    String jsonRequest = mapper.writeValueAsString(mxMessage);
    JsonNode node = rest.doRequest(requestURL, jsonRequest, RestClient.HTTP_METHOD_PUT);

    if (node == null) {
      throw new MatrixClientException("unable to join the room " + room);
    }
  }

  @Override
  public void logout() throws MatrixClientException {

    String requestURL = homeserver + BASE_API + "/logout" + "?access_token=" + accessToken;

    JsonNode node = rest.doRequest(requestURL, "{}", RestClient.HTTP_METHOD_POST);

    if (node == null) {
      throw new MatrixClientException("unable to logout from homeserver");
    }

    rest.close();
    MatrixLogger.finer("logout performed successfully");
  }

  /**
   * Returns a random int (n) within a given range [x,y[
   *
   * @param x int min value
   * @param y int max value
   * @return int with x <= n < y
   */
  private int getRandomInteger(int x, int y) {
    return x + (int) (new Random().nextFloat() * (y - x));
  }
}
