package me.breidenbach.rabbitex.connection;

import com.rabbitmq.client.*;
import me.breidenbach.rabbitex.Consumer;
import me.breidenbach.rabbitex.MessageHandler;

import java.io.IOException;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/1/13
 * Time: 9:17 PM
 * © 2013 Kevin E. Breidenbach
 */
public class RabbitConsumer implements Consumer {
    private static final boolean DURABLE = true;
    private static final boolean AUTO_DELETE = false;
    private static final boolean EXCLUSIVE = false;
    private static final boolean AUTO_ACK = false;

    private final Connection connection;
    private final Channel channel;
    private final MessageHandler handler;
    private final String queue;


    RabbitConsumer(final Connection connection, final String exchange, final String subject,
                   final String queue, final MessageHandler handler) throws RabbitConnectionException {
        this.connection = connection;
        this.handler = handler;
        this.queue = queue;

        try {
            channel = connection.createChannel();
            channel.queueDeclare(queue, DURABLE, EXCLUSIVE, AUTO_DELETE, null);
            channel.queueBind(queue, exchange, subject);

        } catch (IOException e) {
            throw new RabbitConnectionException("Unable to create queue: " + e.getMessage(), e);
        }
    }

    @Override
    public void start() throws IOException {
        channel.basicConsume(queue, AUTO_ACK, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) {
                String json = new String(body);
                MessageWrapper wrapper = MessageWrapper.fromJson(json);
                MessageHandler.Response response = handler.handleMessage(wrapper.getMessage());
            }
        });
    }

    @Override
    public void stop() {
    }
}
