package com.tomcat.application;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Pattern;

import com.tomcat.http.Request;
import com.tomcat.http.Response;
import com.tomcat.servlet.Servlet;

/**
 * 1.创建服务端 用于客户端连接
 * 2.启动tomcat时,读配置文件,这里模拟设置ip地址和端口后以及web.xml配置
 * 3.浏览器发送请求到服务端, 服务端解析客户端http请求.
 *      解析包含http请求类型和请求路径并去配置文件匹配哪个servlet,
 *      此处省略通过路径去匹配部署文件(webapp下的工程)
 * 4.servlet处理完后响应结果给浏览器
 * @author Administrator
 */
public class tomcatServer {
	
	//端口号
	private int port = 8080;
	//ip
	private String ip = "localhost";
	//路径匹配的servlet
	Map<String,Class<?>> servletMapping = new HashMap<String, Class<?>>();
	//配置文件路径
	private String path = this.getClass().getResource("/").getPath();
	
	ExecutorService exec = Executors.newFixedThreadPool(10);
	public tomcatServer(){
		start();
	}

	private void start() {
		init();
		
		//启动tomcat
		try {
			ServerSocket serverSocket = new ServerSocket(this.port,1,InetAddress.getByName(this.ip));
			System.out.println("tomcat 已启动，IP地址:"+this.ip+",监听端口:" + this.port);
			while(true){
				Socket client = serverSocket.accept();
				//等待用户请求
				exec.execute(new Handle(client, servletMapping));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//启动加载配置文件
	private void init() {
		Properties properties = new Properties();
		try {
			FileInputStream fis = new FileInputStream(path + "web.properties");
			properties.load(fis);
			for (Object obj: properties.keySet()) {
				String key = obj.toString();
				if(key.endsWith(".url")){
					String url = properties.getProperty(key);
					String servletName = key.replace("url", "className");
					String className = properties.getProperty(servletName);
					Class<?> clzz = Class.forName(className);
					servletMapping.put(url, clzz);
				}
 			}
			this.port = Integer.valueOf(properties.getProperty("tomcat.port"));
			this.ip = properties.getProperty("tomcat.ip");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) {
		tomcatServer run = new tomcatServer();
	}
}
class Handle implements Runnable{
	private Socket socket;
	Map<String,Class<?>> servletMapping = new HashMap<String, Class<?>>();
	
	public Handle(Socket socket,Map<String,Class<?>> servletMapping){
		this.socket = socket;
		this.servletMapping = servletMapping;
	}
	private void process(Socket client) {
		InputStream is = null; 
		OutputStream os = null; 
		Request request = null;
		Response response = null;
		try {
			is = client.getInputStream();
			os = client.getOutputStream();
			request = new Request(is);
			response = new Response(os);
			String url = request.getRequestUrl();
			boolean bPattern = false;
			boolean bDo = false;
			for (Map.Entry<String, Class<?>> entry : servletMapping.entrySet()) {
				if(entry.getKey().endsWith(".do")){
					if(url.endsWith(".do")){
						bDo = true;
					}
				} 
				if(entry.getKey().endsWith(".action")){
					if(url.endsWith(".action")){
						bDo = true;
					}
				}
				if(entry.getKey().equals("/*")){
					bDo = true;
				}
				if(bDo){
					Servlet servlet = (Servlet) entry.getValue().newInstance();
					servlet.service(request, response);
					bPattern = true;
				}
			}
			if(!bPattern){
				response.writer("<h1>404</h1>");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			response.writer("<h1>500</h1>");
		} finally{
			try {
				if (null != is) {
	                is.close();
	            }
	            if (null != os) {
	                os.close();
	            }

	            if (null != client) {
	                client.close();
	            }
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	@Override
	public void run() {
		process(this.socket);
	}
	
}
