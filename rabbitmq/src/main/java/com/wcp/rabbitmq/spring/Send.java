package com.wcp.rabbitmq.spring;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Send {
	public static void main(String[] args) throws InterruptedException {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:context.xml");
		RabbitTemplate rabbitTemplate = ctx.getBean(RabbitTemplate.class);
		
		rabbitTemplate.convertAndSend("hello rabbitMq");
		
		System.out.println("success");
		
		Thread.sleep(1000);
	}

}
