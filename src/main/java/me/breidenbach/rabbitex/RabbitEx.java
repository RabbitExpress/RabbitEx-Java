package me.breidenbach.rabbitex;

import me.breidenbach.rabbitex.connection.RabbitConnectionException;

import java.io.Closeable;
import java.util.Map;

/**
 * Date: 11/1/13
 * Time: 3:32 PM
 * Copyright 2013 Kevin E. Breidenbach
 * @author Kevin E. Breidenbach
 */

public interface RabbitEx extends Closeable {

    /*
     * Publishes a message to an exchange and subject
     *
     * @param exchange the exchange message will be sent to
     * @param subject the subject the message is related to
     * @param message the message being sent
     * @param options contains map of options {@link Options}
     * @throws RabbitConnectionException any errors from publishing to RabbitMQ
     */
    void publish(final String exchange, final String subject,
                 final String message, final Map<Options, String> options) throws RabbitConnectionException;

    /*
     * Creates a consumer that can listen on a queue that is bound to an exchange and subject
     *
     * @param exchange the exchange to be bound to
     * @param subject the subject to bind to
     * @param the queue name
     * @param handler the implementation of the {@link MessageHandler} that will process the message
     * @return the consumer that will receive the messages from the queue
     * @throws RabbitConnectionException any errors receiving messages
     */
    Consumer consumer(final String exchange, final String subject,
                      final String queue, final MessageHandler handler) throws RabbitConnectionException;

}

