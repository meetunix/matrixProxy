package de.nachtsieb.matrixService.restClient;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.nachtsieb.logging.MatrixLogger;

public class RestClientJersey implements RestClient {

    private Client client ; 		// jax-rs client
	private ObjectMapper mapper; 	// jackson databinding
    
    public RestClientJersey() {
		this.mapper = new ObjectMapper();
        client = ClientBuilder.newClient();
    }

	@Override
	public JsonNode doRequest(String requestURL, String jsonRequest, String method) {

		try {

			WebTarget target = client.target(requestURL);
			Entity<String> entity = Entity.entity(jsonRequest, MediaType.APPLICATION_JSON);
		
			Response response;
			if (method.equals(RestClient.HTTP_METHOD_POST)) {
				response = target.request().post(entity);
			} else  {
				response = target.request().put(entity);
			}
			
			MatrixLogger.finer(String.format("RESPONSE: %d (%s)", response.getStatus(),
        		response.getStatusInfo().getReasonPhrase()));
			
            if (response.getStatus() == 200) {
				JsonNode jsonNode = mapper.readTree(response.readEntity(String.class));
				response.close();
				return jsonNode;
            }else {
            	MatrixLogger.severe(String.format("bad response from homeserver: %d (%s) - %s",
            			response.getStatus(),
            			response.getStatusInfo().getReasonPhrase(),
            			response.readEntity(String.class)));

            	response.close();
            	return null;
            }

		} catch (Exception e) {
			MatrixLogger.severe("unable to send request to homeserver due to exception:");
			MatrixLogger.severe(e.toString());
			MatrixLogger.severe(e.getMessage());
		}

		return null;
	}

	@Override
	public void close() {
		client.close();
	}
}