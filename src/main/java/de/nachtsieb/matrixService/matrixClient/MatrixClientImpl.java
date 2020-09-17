package de.nachtsieb.matrixService.matrixClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.nachtsieb.logging.MatrixLogger;
import de.nachtsieb.matrixService.entities.MatrixLogin;
import de.nachtsieb.matrixService.entities.MatrixMessage;
import de.nachtsieb.matrixService.exceptions.MatrixClientException;
import de.nachtsieb.matrixService.restClient.RestClient;
import de.nachtsieb.matrixService.restClient.RestClientApache;

/**
 * Simple wrapper class for the Matrix client server api. Handles:
 * 
 * * login via password
 * * join a room if not already present
 * * send a message to the room
 * * logout
 *  
 * @author Martin Steinbach
 *
 */

/* used api endpoints with example
 * 
 * /_matrix/client/r0/joined_rooms -> lists all joined rooms (GET)
 * 		https://nachtsieb.de:8448/_matrix/client/r0/joined_rooms?access_token=
 * 
 * /_matrix/client/r0/join/{roomIdOrAlias} (POST) -->
 * 		https://nachtsieb.de:8448/_matrix/client/r0/join/%23klingel%3Anachtsieb.de?access_token=
 * 
 * /_matrix/client/r0/rooms/{roomId}/send/{eventType}/{txnId} (PUT)
 * 		https://nachtsieb.de:8448/_matrix/client/r0/rooms/!ljXeIjSnUvuZzcPcLO%3Anachtsieb.de/send/m.room.message/35?a
 * 
 * /_matrix/client/r0/logout
 * 		https://nachtsieb.de:8448/_matrix/client/r0/logout?access_token=
 */
	

public class MatrixClientImpl implements MatrixClient {

	private final String BASE_API = "/_matrix/client/r0";
	private final String LOGIN_TYPE = "m.login.password";
	
	private String user;
	private String pass;
	private String room;
	private String homeserver;
	
	private String accessToken;
	private String roomID;
	
	private RestClient rest;

	// jackson databinding
	private ObjectMapper mapper;
	

	public MatrixClientImpl(String user, String pass, String room, String homeserver)
			throws JsonProcessingException, URISyntaxException, MatrixClientException {

		this.user = user;
		this.pass = pass;
		this.room = room;
		this.homeserver = homeserver;

		this.rest = new RestClientApache();
		this.mapper = new ObjectMapper();

		this.accessToken = loginAndGetAccessToken();
		this.roomID = joinRoomAndGetRoomID();
	}

	/**
	 * This method performs a login to the homeserver and returns the access token. 
	 * 
	 * @return the access token for further communication
	 * @throws JsonProcessingException 
	 * @throws MatrixClientException 
	 */
	private String loginAndGetAccessToken() throws JsonProcessingException, MatrixClientException  {

		StringBuilder sb = new StringBuilder()
		.append(homeserver)
		.append(BASE_API)
		.append("/login");
		String requestURL = sb.toString();

		MatrixLogin login = new MatrixLogin();
		login.setUser(user);
		login.setPassword(pass);
		login.setType(LOGIN_TYPE);
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
	 * @return the roo id sned by the server
	 * @throws URISyntaxException 
	 * @throws MatrixClientException 
	 */
	private String joinRoomAndGetRoomID () throws URISyntaxException, MatrixClientException{
		
		URI serverURI = new URI(homeserver);
		
		String roomURL = room + ":" + serverURI.getHost();
		roomURL = URLEncoder.encode(roomURL,Charset.forName("UTF-8"));
		
		StringBuilder sb = new StringBuilder()
		.append(homeserver)
		.append(BASE_API)
		.append("/join/")
		.append(roomURL)
		.append("?access_token=")
		.append(accessToken);
		
		String requestURL = sb.toString();
		JsonNode node = rest.doRequest(requestURL, "{}", RestClient.HTTP_METHOD_POST); 
		
		if (node == null) {
			throw new MatrixClientException("unable to join the room " + room);
		} else {
			return node.get("room_id").asText();
		}
	}
	
	@Override
	public void sendMessage(String message) throws JsonProcessingException, MatrixClientException {
			
		StringBuilder sb = new StringBuilder()
		.append(homeserver)
		.append(BASE_API)
		.append("/rooms/")
		.append(roomID)
		.append("/send/m.room.message/")
		.append(getRandomInteger(1, 9999))
		.append("?access_token=")
		.append(accessToken);
	
		String requestURL = sb.toString();

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

		StringBuilder sb = new StringBuilder()
		.append(homeserver)
		.append(BASE_API)
		.append("/logout")
		.append("?access_token=")
		.append(accessToken);

		String requestURL = sb.toString();
		JsonNode node = rest.doRequest(requestURL, "{}", RestClient.HTTP_METHOD_POST);

		if (node == null) {
			throw new MatrixClientException("unable to logout from homeserver");
		} 

		rest.close();
		MatrixLogger.finer("logout performed succesfully");
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
