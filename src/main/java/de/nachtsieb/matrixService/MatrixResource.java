package de.nachtsieb.matrixService;

import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.nachtsieb.logging.MatrixLogger;
import de.nachtsieb.matrixService.entities.Message;
import de.nachtsieb.matrixService.matrixClient.MatrixClient;
import de.nachtsieb.matrixService.matrixClient.MatrixClientImpl;

@Path("transmit")
public class MatrixResource {

    @Inject
    MatrixProxyConfig config;

    @POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/message")
    @Produces(MediaType.TEXT_PLAIN)
    public Response transmitMessage(String postString) {
    	
		try {
			
			// map json-object to java-object
			ObjectMapper mapper = new ObjectMapper();
			Message msg = mapper.readValue(postString, Message.class);

			MatrixLogger.finer("request arrived for room " + msg.getRoom() );
			
			MatrixClient mxClient = new MatrixClientImpl(
					config.getLogin(),
					config.getPassword(),
					msg.getRoom(),
					config.getHomeserver());
			
			mxClient.sendMessage(msg.getMessage());
			
			mxClient.logout();
			
			return Response.noContent().status(Response.Status.OK).build();
			
		} catch (JsonMappingException e) {
			MatrixLogger.severe("unable to bind request to Message class");
			MatrixLogger.severe(e.toString());
		} catch (JsonProcessingException e) {
			MatrixLogger.severe("unable to parse the json-request");
			MatrixLogger.severe(e.toString());
		} catch (URISyntaxException e) {
			MatrixLogger.severe("URI not valid");
			MatrixLogger.severe(e.toString());
		}
		
		// matches if one of the above exceptions is thrown
		return Response.noContent().status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
