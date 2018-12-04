package com.wcp.rabbitmq.sixin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wcp.rabbitmq.util.ConnectionUtil;

public class Send {
	
	private static final String exchange = "exchange_sixin";
	private static final String queue = "queue_sixin";
	private static final String key = "sixin";
	private static final String sixin_exchange = "exchange_dead";
	private static final String sixin_queue = "queue_dead";
	private static final String sixin_key = "dead";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		
		Connection connection = ConnectionUtil.getConnection();
		Channel channel = connection.createChannel();
		
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
		
		String msg = "sixin_queue test";
		channel.basicPublish(exchange, key, null, msg.getBytes());
		
		System.out.println("Send:" + msg);
		
		channel.close();
		connection.close();
		
	}

}
