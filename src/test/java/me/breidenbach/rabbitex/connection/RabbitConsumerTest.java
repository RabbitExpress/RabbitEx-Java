package me.breidenbach.rabbitex.connection;

import com.rabbitmq.client.*;
import me.breidenbach.rabbitex.MessageHandler;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Test;

import java.io.IOException;



import static junit.framework.Assert.assertNotNull;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/3/13
 * Time: 10:44 AM
 * Copyright 2013 Kevin E. Breidenbach
 */
 public class RabbitConsumerTest {

    private static final String QUEUE = "queue";
    private static final String EXCHANGE = "exchange";
    private static final String SUBJECT = "subject";
    private static final String ERROR_EXCHANGE = "ERROR_EXCHANGE";
    private static final String ERROR_SUBJECT = "ERROR_SUBJECT";

    @Mocked RabbitConnection mockRabbitConnection;
    @Mocked Connection mockConnection;
    @Mocked Channel mockChannel;
    @Mocked MessageHandler mockHandler;

    @Test
    public void constructor() throws IOException, RabbitConnectionException {
        new Expectations() {
            {
                mockRabbitConnection.connection(); result = mockConnection;
                mockConnection.createChannel(); result = mockChannel;
                mockChannel.queueDeclare(QUEUE, true, false, false, null); times = 1;
                mockChannel.queueBind(QUEUE, EXCHANGE, SUBJECT);
            }
        };
        assertNotNull(new RabbitConsumer(mockRabbitConnection, EXCHANGE, SUBJECT, QUEUE, mockHandler));
    }

    @Test (expected = RabbitConnectionException.class)
    public void constructorWithError() throws IOException, RabbitConnectionException {
        new Expectations() {
            {
                mockRabbitConnection.connection(); result = mockConnection;
                mockConnection.createChannel(); result = new IOException();
            }
        };
        new RabbitConsumer(mockRabbitConnection, EXCHANGE, SUBJECT, QUEUE, mockHandler);
    }

    @Test
    public void start() throws RabbitConnectionException, IOException {
        new Expectations() {
            {
                mockRabbitConnection.connection(); result = mockConnection;
                mockConnection.createChannel(); result = mockChannel;
                mockChannel.queueDeclare(QUEUE, true, false, false, null); times = 1;
                mockChannel.queueBind(QUEUE, EXCHANGE, SUBJECT);
                mockChannel.basicConsume(QUEUE, false, withInstanceOf(DefaultConsumer.class));
            }
        };

        RabbitConsumer consumer = new RabbitConsumer(mockRabbitConnection, EXCHANGE, SUBJECT, QUEUE, mockHandler);
        consumer.start();
    }

    @Test
    public void handlerAck(@Mocked Envelope mockedEnvelope) throws IOException, RabbitConnectionException {
        new Expectations() {
            {
                mockRabbitConnection.connection(); result = mockConnection;
                mockConnection.createChannel(); result = mockChannel;
                mockChannel.queueDeclare(QUEUE, true, false, false, null); times = 1;
                mockChannel.queueBind(QUEUE, EXCHANGE, SUBJECT);
                mockHandler.handleMessage(anyString); result = MessageHandler.Response.ACK;
                mockChannel.basicAck(anyLong, false);
            }
        };

        RabbitConsumer consumer = new RabbitConsumer(mockRabbitConnection, EXCHANGE, SUBJECT, QUEUE, mockHandler);
        Deencapsulation.invoke(consumer, "handler", mockedEnvelope, message());
    }

    @Test
    public void handlerNackRequeue(@Mocked Envelope mockedEnvelope) throws IOException, RabbitConnectionException {
        new Expectations() {
            {
                mockRabbitConnection.connection(); result = mockConnection;
                mockConnection.createChannel(); result = mockChannel;
                mockChannel.queueDeclare(QUEUE, true, false, false, null); times = 1;
                mockChannel.queueBind(QUEUE, EXCHANGE, SUBJECT);
                mockHandler.handleMessage(anyString); result = MessageHandler.Response.REQUEUE;
                mockChannel.basicNack(anyLong, false, true);
            }
        };

        RabbitConsumer consumer = new RabbitConsumer(mockRabbitConnection, EXCHANGE, SUBJECT, QUEUE, mockHandler);
        Deencapsulation.invoke(consumer, "handler", mockedEnvelope, message());
    }

    @Test
    public void handlerNackReject(@Mocked Envelope mockedEnvelope) throws IOException, RabbitConnectionException {
        new Expectations() {
            {
                mockRabbitConnection.connection(); result = mockConnection;
                mockConnection.createChannel(); result = mockChannel;
                mockChannel.queueDeclare(QUEUE, true, false, false, null); times = 1;
                mockChannel.queueBind(QUEUE, EXCHANGE, SUBJECT);
                mockHandler.handleMessage(anyString); result = MessageHandler.Response.REJECT;
                mockChannel.basicNack(anyLong, false, false);
                mockRabbitConnection.publish(anyString, anyString, HandlerResponse.REJECT(), anyString);
            }
        };

        RabbitConsumer consumer = new RabbitConsumer(mockRabbitConnection, EXCHANGE, SUBJECT, QUEUE, mockHandler);
        Deencapsulation.invoke(consumer, "handler", mockedEnvelope, messageWithErrorExchange());
    }

    private byte[] messageWithErrorExchange() {
        MessageWrapper wrapper = new Message("TEST", ERROR_EXCHANGE, ERROR_SUBJECT);
        return wrapper.toJson().getBytes();
    }

    private byte[] message() {
        MessageWrapper wrapper = new Message("TEST", null, null);
        return wrapper.toJson().getBytes();
    }
}
