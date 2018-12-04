package com.wcp.rabbitmq.util;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ConnectionUtil {
	
	public static Connection getConnection() throws IOException, TimeoutException {
		
		ConnectionFactory factory = new ConnectionFactory();
		
		factory.setHost("127.0.0.1");
		
		//AMQP port
		factory.setPort(5672);
		
		factory.setVirtualHost("/wcp");
		
		factory.setUsername("scott");
		
		factory.setPassword("tiger");
		
		return factory.newConnection();
		
	}

}
