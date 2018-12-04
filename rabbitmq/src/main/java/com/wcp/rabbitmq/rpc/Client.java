package com.wcp.rabbitmq.rpc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import com.wcp.rabbitmq.util.ConnectionUtil;

public class Client {

	private Connection connection;
	private Channel channel;
	private final String queue = "rpc_queue";
	private final String replyQueue = "reply_queue";
	private Consumer consumer;
	private QueueingConsumer con;
	//private String response;
	//private DefaultConsumer con;
	private final BlockingQueue<String> responseq;

	public Client() throws IOException, TimeoutException {
		connection = ConnectionUtil.getConnection();
		channel = connection.createChannel();
		channel.basicQos(1);
		//replyQueue = channel.queueDeclare().getQueue();
		channel.queueDeclare(replyQueue, true, false, false, null);
		con = new QueueingConsumer(channel);
		//channel.basicConsume(replyQueue, true, con);
		// System.out.println(response == null);
		// while(response == null) {
		// channel.basicConsume(replyQueue, true, consumer);
		// }
		 
		//由于我们的消费者交易处理是在单独的线程中进行的，因此我们需要在响应到达之前暂停主线程继续执行call()方法。
	    //这里我们创建的 容量为1的阻塞队列ArrayBlockingQueue，因为我们只需要等待一个响应。
		responseq = new ArrayBlockingQueue<String>(1);
	}

	public String call(String message) throws InterruptedException {
		//System.out.println(response);
		String corrId = UUID.randomUUID().toString();
		BasicProperties properties = new BasicProperties.Builder().correlationId(corrId).replyTo(replyQueue).build();
		try {
			channel.basicPublish("", queue, properties, message.getBytes());
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			System.out.println(31);
		}
		// while (true) {
		
		//System.out.println(11);
		System.out.println(responseq.hashCode());
		
        String c = null;
		try {
			c = channel.basicConsume(replyQueue, true, new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties pro, byte[] body)
						throws IOException {
					//System.out.println(new String(body));
					/*while(true) {
						if (pro.getCorrelationId().equals(corrId)) {
							
							System.out.println(pro.getCorrelationId().equals(corrId));
							
							break;
						}
							
					}*/
					if (pro.getCorrelationId().equals(corrId)) {
						// System.out.println(consumerTag);
						//response = new String(body, "UTF-8");
						// System.out.println(response);
						responseq.offer(new String(body, "UTF-8"));
						System.out.println(new String(body));
						//channel.basicAck(envelope.getDeliveryTag(), false);
					}
				}
			});
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println(41);
		}
		// }
		
		//System.out.println(2);
        
        //String response = responseq.take();
		
        //取消掉已经完成任务的第一个消费者，不然两个消费者同时竞争，第二个可能会拿不到正确的数据
        try {
			//String response = responseq.take();
			channel.basicCancel(c);
			//return response;
		} catch (Exception e1) {
			//TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println(51);
			//return null;
		}
		/*try {
			String response = responseq.take();
	        System.out.println(response);
	        return response;
			//return null;
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println(61);
			return null;
		}*/
        
		return responseq.poll();
        //return response;
	}

	public String call1(String message)
			throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		String res = null;
		String corrId = UUID.randomUUID().toString();
		BasicProperties properties = new BasicProperties.Builder().correlationId(corrId).replyTo(replyQueue).build();
		channel.basicPublish("", queue, properties, message.getBytes());
		while (true) {
			QueueingConsumer.Delivery delivery = con.nextDelivery();
			if (delivery.getProperties().getCorrelationId().equals(corrId)) {
				res = new String(delivery.getBody());
				break;
			}
		}
		return res;

	}
	
	public String call2(String message) throws IOException, InterruptedException {
	    final String corrId = UUID.randomUUID().toString();

	    //String replyQueue = channel.queueDeclare().getQueue();
	    BasicProperties props = new BasicProperties
	            .Builder()
	            .correlationId(corrId)
	            .replyTo(replyQueue)
	            .build();

	    channel.basicPublish("", queue, props, message.getBytes("UTF-8"));

	    final BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);
	    System.out.println(response.hashCode());

	    String ctag = channel.basicConsume(replyQueue, true, new DefaultConsumer(channel) {
	      @Override
	      public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
	        if (properties.getCorrelationId().equals(corrId)) {
	          response.offer(new String(body, "UTF-8"));
	        }
	      }
	    });

	    String result = response.take();
	    channel.basicCancel(ctag);
	    return result;
	  }
	

	public void clean() {
		//response = null;
	}

	public void close() throws IOException {
		connection.close();
	}

	public static void main(String[] args) throws IOException, TimeoutException, ShutdownSignalException,
			ConsumerCancelledException, InterruptedException {
		Client client = new Client();
		System.out.println(" [x] Requesting fib(5)");
		String s = client.call2("5");
		System.out.println(" [.] Got '" + s + "'");

		//client.clean();
		System.out.println(" [x] Requesting fib(6)");
		String s1 = client.call2("6");
		System.out.println(" [.] Got '" + s1 + "'");
		
		for(int i = 0; i < 40; i++) {
			String is = Integer.toString(i);
			System.out.println(" [x] Requesting fib(" + is + ")");
			String an = client.call(is);
			//System.out.println(".");
			//System.out.println(client.responseq.poll());
			System.out.println(" [.] Got '" + an + "'");
		}
		
		for(String re: client.responseq) {
			System.out.println(re);
		}

		client.close();

	}
}
