package com.wcp.rabbitmq.confirm;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wcp.rabbitmq.util.ConnectionUtil;

public class Send2 {

	private static String queue = "confirm_queue";

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

		Connection connection = ConnectionUtil.getConnection();

		Channel channel = connection.createChannel();

		channel.queueDeclare(queue, false, false, false, null);

		String msg = "confirm queue test";

		channel.confirmSelect();

		for (int i = 0; i < 10; i++) {
			channel.basicPublish("", queue, null, msg.getBytes());
			//int j = 1/0;
		}

		if (channel.waitForConfirms()) {
			System.out.println("send message: " + msg + " OK");
		} else {
			System.out.println("send message: " + msg + " fail");
		}

		channel.close();

		connection.close();
	}

}
