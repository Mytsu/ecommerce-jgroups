package system;

import java.util.ArrayList;

public class Comunication {

	public EnumChannel channel;
	public EnumServices service;
	public ArrayList<Object> content;
	
	public Comunication() {
		content = new ArrayList<Object>();
	}
	
	public Comunication(EnumChannel channel, EnumServices service, ArrayList<Object> content) {
		this.channel = channel;
		this.service = service;
		this.content = content;
	}
	
}
