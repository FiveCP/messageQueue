package com.wcp.rabbitmq.confirm;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.wcp.rabbitmq.util.ConnectionUtil;

public class Send3 {

	private static String queue = "confirm_queue";

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		
		Connection connection = ConnectionUtil.getConnection();
		
		Channel channel = connection.createChannel();
		
		channel.queueDeclare(queue, false, false, false, null);
		
		channel.confirmSelect();
		
		final SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<Long>());
		
		channel.addConfirmListener(new ConfirmListener() {

			public void handleAck(long deliverTag, boolean multiple) throws IOException {
				//接收批量序号
				if(multiple) {
					System.out.println("----handleAck----multiple ");
					confirmSet.headSet(deliverTag+1).clear();
				}else {
					System.out.println("----handleAck----single ");
					confirmSet.remove(deliverTag);
				}
				
			}

			public void handleNack(long deliverTag, boolean multiple) throws IOException {
				//接收批量序号
				if(multiple) {
					System.out.println("----handleNack----multiple ");
					confirmSet.headSet(deliverTag+1).clear();
				}else {
					System.out.println("----handleNack----single ");
					confirmSet.remove(deliverTag);
				}
				
			}
			
		});
		
		String msg = "confirm queue syn test";
		
		for(int i = 0; i < 100; i++) {
			long seqNo = channel.getNextPublishSeqNo();
			channel.basicPublish("", queue, null, (msg + " " + i).getBytes());
			confirmSet.add(seqNo);
		}
		
		channel.close();
		connection.close();

	}

}
