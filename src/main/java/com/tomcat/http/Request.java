package com.tomcat.http;

import java.io.InputStream;

public class Request {
	private String requestType;
	private String requestUrl;
	
	public Request(InputStream is){
		try {
			//解析客户端信息
			String content = "";
			byte[] buff = new byte[1024];
			
			int len = 0;
			if((len = is.read(buff)) > 0){
				content = new String(buff,0,len);
			}
			System.out.println(content);
			
			String line = content.split("\\n")[0];

            String[] arr = line.split("\\s");

            this.requestType = arr[0];

            this.requestUrl = arr[1].split("\\?")[0];
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	
	
}
