package com.tomcat.servlet;

import com.tomcat.http.Request;
import com.tomcat.http.Response;

public class DispatcherServlet extends Servlet{
	
	@Override
	public void get(Request request, Response response) {
		this.post(request, response);
	}

	@Override
	public void post(Request request, Response response) {
		//处理逻辑
        response.writer("<h1>hello world</h1>");
	}
}
