package me.breidenbach.rabbitex.connection;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import me.breidenbach.rabbitex.Consumer;
import me.breidenbach.rabbitex.Options;
import me.breidenbach.rabbitex.RabbitEx;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/1/13
 * Time: 3:59 PM
 * © 2013 Kevin E. Breidenbach
 */
public class RabbitConnection implements RabbitEx {
    private static final boolean MANDATORY = true;

    private final RabbitConnectionCache cache;
    private final ConnectionFactory factory;

    private final Connection connection; //the underlying RabbitConnection

    private boolean closed = false;

    RabbitConnection(RabbitConnectionCache cache, ConnectionFactory factory) throws RabbitConnectionException {
        this.cache = cache;
        this.factory = factory;
        this.connection = prepareRabbitConnection();
    }

    @Override
    public void close() throws IOException {
        connection.close();
        cache.remove(factory.getHost(), factory.getPort(), factory.getVirtualHost(), factory.getUsername());
        closed = true;
    }

    @Override
    public void publish(String exchange, String subject, String message, Map<Options, String> options) throws RabbitConnectionException {
        MessageWrapper wrapper = createWrapper(message, MessageWrapper.MessageType.MESSAGE, options);
        publishMessage(exchange, subject, wrapper);
    }

    @Override
    public Consumer consumer(String exchange, String subject, String queue) throws RabbitConnectionException {

        return null;
    }

    public boolean isClosed() {
        return closed;
    }

    void publish_error(String exchange, String subject, String message) throws RabbitConnectionException {
        MessageWrapper wrapper = createWrapper(message, MessageWrapper.MessageType.ERROR, null);
        publishMessage(exchange, subject, wrapper);
    }

    private void publishMessage(String exchange, String subject, MessageWrapper wrapper) throws RabbitConnectionException {
        AMQP.BasicProperties.Builder builder =  new AMQP.BasicProperties.Builder();
        String json = wrapper.toJson();
        builder.deliveryMode(2);
        builder.timestamp(Calendar.getInstance().getTime());
        try {
            Channel channel = connection.createChannel();
            channel.basicPublish(exchange, subject, MANDATORY, builder.build(), json.getBytes());
        } catch (IOException e) {
            throw new RabbitConnectionException("Unable to publish message", e);
        }
    }

    private Connection prepareRabbitConnection() throws RabbitConnectionException {
        try {
            return factory.newConnection();
        } catch (IOException e) {
            throw new RabbitConnectionException("Unable to create connection: " + e.getMessage(), e);
        }
    }

    private MessageWrapper createWrapper(String message, MessageWrapper.MessageType type, Map<Options,String> options) {
        MessageWrapper wrapper = new MessageWrapper(message, type);
        if (options != null) {
            for (Options key: options.keySet()) {
                switch (key) {
                    case ERROR_EXCHANGE:
                        wrapper.setErrorExchange(options.get(key));
                        break;
                    case ERROR_SUBJECT:
                        wrapper.setErrorSubject(options.get(key));
                        break;
                }
            }
        }
        return wrapper;
    }
}