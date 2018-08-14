package com.tomcat.servlet;

import com.tomcat.http.Request;
import com.tomcat.http.Response;

public abstract class Servlet {
	public abstract void get(Request request,Response response);
	public abstract void post(Request request,Response response);
	
	public void service(Request request,Response response){
		if("GET".equals(request.getRequestType())
				|| "get".equals(request.getRequestType())){
			this.get(request, response);
		} else {
			this.post(request, response);
		}
	}
}
