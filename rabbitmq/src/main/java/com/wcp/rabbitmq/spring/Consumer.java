package com.wcp.rabbitmq.spring;

public class Consumer {

	public void listen(String msg) {
		System.out.println("消费者: " + msg);
	}
}
