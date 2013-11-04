import me.breidenbach.rabbitex.MessageHandler;
import me.breidenbach.rabbitex.RabbitEx;
import me.breidenbach.rabbitex.connection.RabbitConnectionException;
import me.breidenbach.rabbitex.connection.RabbitConnectionFactory;

import java.io.IOException;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/3/13
 * Time: 1:46 PM
 * Copyright 2013 Kevin E. Breidenbach
 */
public class ConsumerTest {
    private static final String HOSTNAME = "127.0.0.1";
    private static final int PORT = 5672;
    private static final String EXCHANGE = "my-exchange";
    private static final String SUBJECT = "my-subject";
    private static final String QUEUE = "my-queue";

    public void consume(MessageHandler myHandler) throws RabbitConnectionException, IOException {
        RabbitEx rabbitEx = new RabbitConnectionFactory().rabbitConnection(HOSTNAME, PORT);
        rabbitEx.consumer(EXCHANGE, SUBJECT, QUEUE, myHandler).start();
    }

    public static void main(String...args) {
        ConsumerTest test = new ConsumerTest();
        try {
            test.consume(new MyHandler());
        } catch (RabbitConnectionException|IOException e) {
            e.printStackTrace();
        }
    }

    private static class MyHandler implements MessageHandler {

        int messageNumber = 1;
        @Override
        public Response handleMessage(String message) {
            System.out.println("Message Number: " + messageNumber++ + ": " + message);
            return Response.ACK;
        }
    }
}
