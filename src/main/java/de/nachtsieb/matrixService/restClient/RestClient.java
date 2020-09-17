package de.nachtsieb.matrixService.restClient;

import com.fasterxml.jackson.databind.JsonNode;

public interface RestClient {

	public static final String HTTP_METHOD_POST = "POST";
	public static final String HTTP_METHOD_PUT = "PUT";

	public JsonNode doRequest(String requestURL, String jsonRequest, String method);

	public void close();

}
