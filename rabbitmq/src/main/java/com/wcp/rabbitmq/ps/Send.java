package com.wcp.rabbitmq.ps;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ReturnListener;
import com.wcp.rabbitmq.util.ConnectionUtil;

public class Send {

	private static final String exchange = "fanout_exchange";

	public static void main(String[] args) throws IOException, TimeoutException {

		Connection connection = ConnectionUtil.getConnection();

		Channel channel = connection.createChannel();

		channel.exchangeDeclare(exchange, "direct", false);

		String msg = "fanout_exchange send";

		channel.basicPublish(exchange, "second", true, null, msg.getBytes());

		channel.addReturnListener(new ReturnListener() {

			@Override
			public void handleReturn(int replyCode, String replyText, String exchange, String routingKey,
					BasicProperties basicProperties, byte[] returnMsg) throws IOException {
				System.out.println("Basic.Return 返回的结果是: " + new String(returnMsg));
			}

		});

		System.out.println("Send: " + msg);

		channel.close();

		connection.close();
	}

}
