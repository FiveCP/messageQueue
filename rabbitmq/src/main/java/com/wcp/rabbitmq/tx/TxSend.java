package com.wcp.rabbitmq.tx;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wcp.rabbitmq.util.ConnectionUtil;

public class TxSend {
	
	private static String queue = "tx_queue";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		
		Connection connection = ConnectionUtil.getConnection();
		
		Channel channel = connection.createChannel();
		
		channel.queueDeclare(queue, false, false, false, null);
		
		String msg = "simple queue test";
		
		try {
			channel.txSelect();
			
			channel.basicPublish("", queue, null, msg.getBytes());
			
			int i = 1 / 0;
			
			System.out.println("send message: " + msg);
			
			channel.txCommit();
			
		} catch (Exception e) {
			
			channel.txRollback();
			
			System.out.println("roolback msg: " + msg);
		}
			
		channel.close();
		
		connection.close();
	}

}
