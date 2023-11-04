package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.StandardCharsets;

public class NewTask {
    private static final String TASK_QUEUE_NAME = "new_task_queue";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            boolean durable = true; //the queue will survive a RabbitMQ node restart.

            channel.queueDeclare(TASK_QUEUE_NAME, durable, false, false, null);

            String message = String.join(" ", argv);

            int prefetchCount = 1;
            channel.basicQos(prefetchCount); /*This tells RabbitMQ not to give more than 1 message to a worker at a time.
            don't dispatch a new message to a worker until it has processed and acknowledged the previous one.
            Instead, it will dispatch it to the next worker that is not still busy.*/

            channel.basicPublish("", TASK_QUEUE_NAME, // "" here is for basic exchanger
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    /*Marking messages as persistent doesn't fully guarantee that a message won't be lost.
                    Although it tells RabbitMQ to save the message to disk,*/
                    message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}