package de.nachtsieb.matrixService.restClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.nachtsieb.logging.MatrixLogger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestClientOld implements RestClient {

	// jackson databinding
	private final ObjectMapper mapper;
	
	public RestClientOld() {
		this.mapper = new ObjectMapper();
	}

	public JsonNode doRequest(String requestURL, String jsonRequest, String method) {
		
		try {

			URL url = new URL(requestURL);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestMethod(method);
			connection.setDoOutput(true);
			
			byte[] bytes = jsonRequest.getBytes();
            connection.setFixedLengthStreamingMode(bytes.length);

			OutputStream out = connection.getOutputStream();
            out.write(bytes);
            out.flush();
            out.close();
            
            int responseCode = connection.getResponseCode();
            
			BufferedReader br = new BufferedReader(
					new InputStreamReader(connection.getInputStream())); 
			
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			String response = sb.toString();
			
			br.close();

            if (responseCode == 200) {
							return mapper.readTree(response);
            }else {
            	MatrixLogger.severe("bad response from homeserver: " + response);
            	return null;
            }

		} catch (IOException e) {
			MatrixLogger.severe("unable to send request to homeserver due to exception:");
			MatrixLogger.severe(e.toString());
		}
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
