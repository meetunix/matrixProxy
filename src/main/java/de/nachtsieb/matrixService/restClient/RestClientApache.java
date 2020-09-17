package de.nachtsieb.matrixService.restClient;

import java.io.IOException;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.nachtsieb.logging.MatrixLogger;

public class RestClientApache implements RestClient {

	// jackson databinding
	private ObjectMapper mapper;

	private CloseableHttpClient client;

	public RestClientApache() {
		this.mapper = new ObjectMapper();
		this.client = HttpClients.createDefault();

	}

	@Override
	public JsonNode doRequest(String requestURL, String jsonRequest, String method) {
		
		StringEntity body;
		CloseableHttpResponse response = null;

		try {
			

			body = new StringEntity(jsonRequest);
			
			if (method.equals(RestClient.HTTP_METHOD_POST)) {
				
				HttpPost httpPost = new HttpPost(requestURL);
				httpPost.setEntity(body);
			    httpPost.setHeader("Accept", "application/json");
			    httpPost.setHeader("Content-type", "application/json");

			    response = client.execute(httpPost);

			} else if (method.equals(RestClient.HTTP_METHOD_PUT)) {

				HttpPut httpPut = new HttpPut(requestURL);
				httpPut.setEntity(body);
			    httpPut.setHeader("Accept", "application/json");
			    httpPut.setHeader("Content-type", "application/json");

			    response = client.execute(httpPut);
			} 
			
			if (response.getCode() == 200) {
				
				HttpEntity respEntity = response.getEntity();
				
				String responseString = EntityUtils.toString(respEntity);
				respEntity.close();
				
				response.close();

				JsonNode jsonNode = mapper.readTree(responseString);
				return jsonNode;
				
			}else {
            	MatrixLogger.severe("bad response from homeserver: " + response);
            	return null;
			}
			
		} catch (Exception e) {
			MatrixLogger.severe("unable to send request to homeserver due to exception:");
			MatrixLogger.severe(e.toString());
		}
		
		return null;
	}
	
	public void close() {

		try {
			client.close();
		} catch (IOException e) {
			MatrixLogger.severe("unable to logout from homeserver due to exception:");
			MatrixLogger.severe(e.toString());
		}
	}

}
