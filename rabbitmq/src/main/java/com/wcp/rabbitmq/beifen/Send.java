package com.wcp.rabbitmq.beifen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wcp.rabbitmq.util.ConnectionUtil;

public class Send {
	
	private static final String exchange_beifen = "alternate_exchange";
	private static final String queue_beifen = "alternate_queue";
	private static final String exchange = "exchange_beifen";
	private static final String queue = "queue_beifen";
	private static final String key = "beifen";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		
		Connection connection = ConnectionUtil.getConnection();
		Channel channel = connection.createChannel();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("alternate-exchange", exchange_beifen);
		
		//这种是将消息队列的消息设置统一的过期时间，所以队列已过期的消息一定在队列的头部，因此时间RabbitMQ会
		//定期扫描队列头部，因此消息已过期就会被队列移除
		Map<String, Object> map1 = new HashMap<>();
		map1.put("x-message-ttl", 6000);
		//设置队列的过期时间（未被使用和重新声明的时间，到期后自动删除）
		map.put("x-expires", 1800000);
		
		channel.exchangeDeclare(exchange, "direct", true, false, map);
		channel.exchangeDeclare(exchange_beifen, "fanout", true, false, null);
		
		channel.queueDeclare(queue, true, false, false, null);
		channel.queueDeclare(queue_beifen, false, false, false, map1);
		
		channel.queueBind(queue, exchange, key);
		channel.queueBind(queue_beifen, exchange_beifen, "");
		
		//设置某条消息的过期时间，同时用上面的方法和这种方法时会选择时间小的那个时间充当过期时间。因为每条消息的过期时间不同，
		//所以当消息被消费时才会判断是否过期，故过期的消息不一定立刻就被队列删除
		AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
		builder.deliveryMode(2);
		builder.expiration("5000");
		AMQP.BasicProperties properties = builder.build();
		
	    String msg = "Send: beifen test";
	    channel.basicPublish(exchange, key, properties, msg.getBytes());
	    
	    System.out.println(msg);
	    
	    channel.close();
	    connection.close();
	    
	}

}
