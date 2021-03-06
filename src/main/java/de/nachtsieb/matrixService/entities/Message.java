package de.nachtsieb.matrixService.entities;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * Represents the incomming message that will be redirected to the Matrix homeserver. 
 */
@XmlRootElement
public class Message {
	
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private String timeString;
	private String message;
	private String room;
	private SimpleDateFormat sdf;

	public Message() { 
		setTimeString();
	}
	
	private void setTimeString() {
		sdf = new SimpleDateFormat(DATE_FORMAT);
		this.timeString = sdf.format(new Date());
	}
	
	
	/** GETTER/SETTER **/
	
	public String getTimeString() {
		return timeString;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder("---- MESSAGE ----");
		sb.append(String.format("\n%-10s %20s\n","TIME:" , timeString));
		sb.append(String.format("%-10s %20s\n","ROOM:", room));
		sb.append(String.format("%-10s\n%s\n","MESSAGE:", message));
		
		return sb.toString();
	}

}
