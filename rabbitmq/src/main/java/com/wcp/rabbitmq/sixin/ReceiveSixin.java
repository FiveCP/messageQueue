package com.wcp.rabbitmq.sixin;

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

public class ReceiveSixin {
	
	private static final String exchange = "exchange_sixin";
	private static final String queue = "queue_sixin";
	private static final String key = "sixin";
	private static final String sixin_exchange = "exchange_dead";
	private static final String sixin_queue = "queue_dead";
	private static final String sixin_key = "dead";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		
		Connection connection = ConnectionUtil.getConnection();
		Channel channel =  connection.createChannel();
		
		Map<String, Object> map = new HashMap<>();
		map.put("x-message-ttl", 10000);
		map.put("x-dead-letter-exchange", sixin_exchange);
		map.put("x-dead-letter-routing-key", sixin_key);
		
		channel.exchangeDeclare(exchange, "direct", true, false, null);
		channel.queueDeclare(queue, true, false, false, map);
		channel.queueBind(queue, exchange, key);
		
		channel.exchangeDeclare(sixin_exchange, "direct", true, false, null);
		channel.queueDeclare(sixin_queue, true, false, false, null);
		channel.queueBind(sixin_queue, sixin_exchange, sixin_key);
			
		channel.basicConsume(sixin_queue, new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				String msg = new String(body, "utf-8");
				System.out.println("receive msg from sixin_queue: " + msg);
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

