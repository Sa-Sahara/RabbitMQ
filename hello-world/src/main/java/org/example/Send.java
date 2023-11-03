package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/*
 * Make sure docker container with RabbitMQ is running:
 * docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.12-management
 *
 * external libs saved to:
 * C:\Users\user\IdeaProjects\coursera\RabbitMQ\hello-world\src\main\java
 *
 * cd C:\Users\user\IdeaProjects\coursera\RabbitMQ\hello-world\src\main\java\org\example
 *
 * compile both classes if not yet:
 * javac -cp amqp-client-5.16.0.jar Send.java Recv.java
 *
 * run Send
 * java -cp .;amqp-client-5.16.0.jar;slf4j-api-1.7.36.jar;slf4j-simple-1.7.36.jar org.example.Send
 * */

public class Send {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World!";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}
