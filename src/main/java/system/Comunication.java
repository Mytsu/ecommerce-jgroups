package system;

import java.util.ArrayList;

public class Comunication {

	public String channel;
	public String service;
	public ArrayList<Object> content;
	
	public Comunication(String channel, String service, ArrayList<Object> content) {
		this.channel = channel;
		this.service = service;
		this.content = content;
	}
	
}
