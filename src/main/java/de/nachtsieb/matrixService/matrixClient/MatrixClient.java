package de.nachtsieb.matrixService.matrixClient;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.nachtsieb.matrixService.exceptions.MatrixClientException;

public interface MatrixClient {
	
	/**
	 * Sends a massage to a previously performed login.
	 * 
	 * @param message
	 * @throws JsonProcessingException
	 * @throws MatrixClientException 
	 */
	public void sendMessage(String message) throws JsonProcessingException, MatrixClientException;
	
	/**
	 * Performs a logout out for the current used session.
	 * @throws MatrixClientException 
	 * 
	 */
	public void logout() throws MatrixClientException;

}
