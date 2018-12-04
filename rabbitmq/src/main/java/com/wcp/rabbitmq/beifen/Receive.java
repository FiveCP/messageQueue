package com.wcp.rabbitmq.beifen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.wcp.rabbitmq.util.ConnectionUtil;

public class Receive {
	
	private static final String exchange_beifen = "alternate_exchange";
	private static final String exchange = "exchange_beifen";
	private static final String queue = "queue_beifen";
	private static final String key = "beifen";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		
		Connection connection = ConnectionUtil.getConnection();
		Channel channel =  connection.createChannel();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("alternate-exchange", exchange_beifen);
		channel.exchangeDeclare(exchange, "direct", true, false, map);	
		channel.queueDeclare(queue, true, false, false, null);
		
		channel.basicConsume(queue, new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				String msg = new String(body, "utf-8");
				System.out.println("receive msg: " + msg);
				channel.basicAck(envelope.getDeliveryTag(), false);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}
