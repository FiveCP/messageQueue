package com.wcp.rabbitmq.work;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wcp.rabbitmq.util.ConnectionUtil;

public class Send {
	
	private final static String queue = "work_queue";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		
		Connection connection = ConnectionUtil.getConnection();
		
		Channel channel = connection.createChannel();
		
		channel.queueDeclare(queue, false, false, false, null);
		
		for(int i = 0; i < 50; i++) {
			String msg = "work_queue send: " + i;
			channel.basicPublish("", queue, null, msg.getBytes());
			System.out.println("send : " + msg);
			try {
				Thread.sleep(i*20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		channel.close();
		connection.close();
	}
	

}
