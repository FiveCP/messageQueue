package com.wcp.rabbitmq.simple;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import com.wcp.rabbitmq.util.ConnectionUtil;

public class Receive {

private static final String queue = "simple_queue";

	public static void main(String[] args) throws IOException, TimeoutException {

		Connection connection = ConnectionUtil.getConnection();

		Channel channel = connection.createChannel();

		channel.basicQos(64);

		channel.queueDeclare(queue, false, false, false, null);

		Consumer consumer = new DefaultConsumer(channel) {

			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				channel.basicReject(envelope.getDeliveryTag(),false);
				String msg = new String(body, "utf-8");
				System.out.println("receive msg: " + msg);
			}
		};
		
		channel.basicConsume(queue, false, consumer);
		//connection.close();
		//ShutdownSignalException s = channel.getCloseReason();
		//s.printStackTrace();

	}

}
