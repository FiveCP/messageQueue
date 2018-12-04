package com.wcp.rabbitmq.rpc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.wcp.rabbitmq.util.ConnectionUtil;

public class Server {

	private static final String queue = "rpc_queue";

	public static void main(String[] args) throws IOException, TimeoutException {

		Connection connection = ConnectionUtil.getConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(queue, true, false, false, null);
		channel.basicQos(1);
		System.out.println(" [x] Awaiting RPC requests");

		Consumer consumer = new DefaultConsumer(channel) {

			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				BasicProperties replyPro = new BasicProperties.Builder().correlationId(properties.getCorrelationId())
						.build();
				String response = "";

				try {
					String message = new String(body, "UTF-8");
					int n = Integer.parseInt(message);
					System.out.println(" [.] fib(" + message + ")");
					response += fib(n);
				} catch (RuntimeException e) {
					System.out.println(" [.] " + e.toString());
				} finally {
					channel.basicPublish("", properties.getReplyTo(), replyPro, response.getBytes());
					channel.basicAck(envelope.getDeliveryTag(), false);
				}
			}
		};
		
		channel.basicConsume(queue, false, consumer);
	}
	
	public static int fib(int n) {
		if(n == 0)
			return 0;
		if(n == 1)
			return 1;
		return fib(n-1) + fib(n-2);
	}
	
	

}
