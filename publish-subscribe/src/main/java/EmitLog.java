/* in RabbitMQ is that the producer never sends any messages directly to a queue.
Actually, quite often the producer doesn't even know
if a message will be delivered to any queue at all.*/
/*
* cd java
* set CP=.;amqp-client-5.16.0.jar;slf4j-api-1.7.36.jar;slf4j-simple-1.7.36.jar
* javac -cp %CP% EmitLog.java ReceiveLogs.java
*
* */

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

public class EmitLog {

    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            /*publishing to a non-existing exchange is forbidden.
            * The messages will be lost if no queue is bound to the exchange yet,
            * but that's okay for us;
            * if no consumer is listening yet we can safely discard the message.*/
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

            /*In the Java client, when we supply no parameters to queueDeclare()
            we create a non-durable, exclusive, autodelete queue
            with a generated name*/

            String message = argv.length < 1 ? "info: Hello World!" :
                    String.join(" ", argv);

            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }

}