package com.wcp.rabbitmq.ps;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.wcp.rabbitmq.util.ConnectionUtil;

public class Receive2 {

private final static String queue = "fanout_queue_second";
private static final String exchange = "fanout_exchange";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		
		Connection connection = ConnectionUtil.getConnection();
		
		final Channel channel = connection.createChannel();
		
		channel.exchangeDeclare(exchange, "direct", false);
		
		channel.queueDeclare(queue, false, false, false, null);
		
		channel.queueBind(queue, exchange, "second");
		
		channel.basicQos(1);
		
		Consumer consumer = new DefaultConsumer(channel) {
			
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				String msg = new String(body, "utf-8");
				System.out.println("receive msg: " + msg);
				channel.basicAck(envelope.getDeliveryTag(), false);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					System.out.println("[2] done : " + msg );
					System.out.println("");
				}
			}					
		};
		
		channel.basicConsume(queue, consumer);
	}

}

