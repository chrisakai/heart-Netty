package com.wlz.server;

import com.wlz.server.typeServer.TCPServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;


import java.util.ArrayList;
import java.util.List;


/**
 *  服务器启动类
  * @Author: myzf
  * @Date: 2019/2/23 13:00
  * @param
*/
@SpringBootApplication
@PropertySource(value= "classpath:/application.properties")
@Deprecated
public class ServerApplication {

	public static void main(String[] args) throws Exception {

		//SpringApplication.run(ServerApplication.class, args);
		 ConfigurableApplicationContext context = SpringApplication.run(ServerApplication.class, args);
		 TCPServer tcpServer = context.getBean(TCPServer.class);
		openBrowse("http://localhost:10003/monitor.do");
		tcpServer.start();


	}


	/**
	 * @title 使用默认浏览器打开
	 * @param url 要打开的网址
	 */
	private static void openBrowse(String url) throws Exception {
		List<String> list = new ArrayList<String>();
		list.add("cmd");
		list.add("/c");
		list.add("start");
		list.add(url);
		list.toArray();
		Runtime.getRuntime().exec(list.toArray(new String[0]));

	}


}
