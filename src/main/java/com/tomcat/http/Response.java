package com.tomcat.http;

import java.io.OutputStream;

public class Response {
	
	private OutputStream os;
	
	public static final String HEADER = "HTTP/1.1 200 \r\n"
            + "Content-Type: text/html\r\n"
            + "\r\n";
	
	public Response(OutputStream os){
		this.os = os;
	}
	
	public void writer(String msg){
		try {
			msg = HEADER + msg;
			os.write(msg.getBytes());
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
