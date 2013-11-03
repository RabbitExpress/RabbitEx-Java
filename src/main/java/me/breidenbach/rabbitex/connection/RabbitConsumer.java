package me.breidenbach.rabbitex.connection;

import com.rabbitmq.client.*;
import me.breidenbach.rabbitex.Consumer;
import me.breidenbach.rabbitex.MessageHandler;

import java.io.IOException;

/**
 * Date: 11/1/13
 * Time: 9:17 PM
 * © 2013 Kevin E. Breidenbach
 * @author Kevin E. Breidenbach
 */
public class RabbitConsumer implements Consumer {
    private static final boolean DURABLE = true;
    private static final boolean AUTO_DELETE = false;
    private static final boolean EXCLUSIVE = false;
    private static final boolean AUTO_ACK = false;

    private final Channel channel;
    private final MessageHandler handler;
    private final String queue;
    private final RabbitConnection connection;


    RabbitConsumer(final RabbitConnection connection, final String exchange, final String subject,
                   final String queue, final MessageHandler handler) throws RabbitConnectionException {
        this.connection = connection;
        this.handler = handler;
        this.queue = queue;

        try {
            channel = connection.connection().createChannel();
            channel.queueDeclare(queue, DURABLE, EXCLUSIVE, AUTO_DELETE, null);
            channel.queueBind(queue, exchange, subject);

        } catch (IOException e) {
            throw new RabbitConnectionException("Unable to create queue: " + e.getMessage(), e);
        }
    }

    @Override
    public void start() throws IOException {
        DefaultConsumer defaultConsumer = createDefaultConsumer();
        channel.basicConsume(queue, AUTO_ACK, defaultConsumer);
    }

    private DefaultConsumer createDefaultConsumer() {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                handler(envelope, body);
            }
        };
    }

    private void handler(Envelope envelope, byte[] body) throws IOException {
        String json = new String(body);
        long deliveryTag = envelope.getDeliveryTag();
        MessageWrapper wrapper = MessageWrapper.fromJson(json);
        MessageHandler.Response response = handler.handleMessage(wrapper.getMessage());

        try {
            switch (response) {
                case ACK:
                    channel.basicAck(deliveryTag, false);
                    break;
                case REQUEUE:
                    channel.basicNack(deliveryTag, false, true);
                    sendErrorMessage(wrapper, response);
                    break;
                case REJECT:
                    channel.basicNack(deliveryTag, false, false);
                    sendErrorMessage(wrapper, response);
                    break;
            }
        } catch (RabbitConnectionException e) {
            // we can't do a lot with this, so eat it.
            e.printStackTrace();
        }
    }

    private void sendErrorMessage(MessageWrapper wrapper, MessageHandler.Response response) throws RabbitConnectionException {
        String exchange = wrapper.getErrorExchange();
        String subject = wrapper.getErrorSubject();
        if (exchange != null) {
            connection.publishError(exchange, subject, response, wrapper.getMessage());
        }
    }
}
