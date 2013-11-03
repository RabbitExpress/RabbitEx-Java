package me.breidenbach.rabbitex.connection;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import me.breidenbach.rabbitex.Consumer;
import me.breidenbach.rabbitex.MessageHandler;
import me.breidenbach.rabbitex.Options;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/2/13
 * Time: 12:13 PM
 * © 2013 Kevin E. Breidenbach
 */
public class RabbitConnectionTest {

    private static final String EXCHANGE = "exchange";
    private static final String SUBJECT = "subject";
    private static final String QUEUE = "queue";
    private static final String MESSAGE = "message";
    private static final String ERROR_EXCHANGE = "ERROR_EXCHANGE";
    private static final String ERROR_SUBJECT = "ERROR_SUBJECT";
    private static final boolean MANDATORY = true;
    private static final String JSON = "{}";
    private static final String EXCHANGE_TYPE = "topic";


    private RabbitConnection testConnection;

    @Mocked ConnectionFactory mockedFactory;
    @Mocked RabbitConnectionCache mockedCache;
    @Mocked Connection mockedConnection;
    @Mocked Channel mockedChannel;
    @Mocked MessageWrapper mockedWrapper;


    @Test
    public void constructor() throws RabbitConnectionException, IOException {
        new Expectations() {
            {
                mockedFactory.newConnection(); result = mockedConnection;
            }
        };
        testConnection = new RabbitConnection(mockedCache, mockedFactory);
    }

    @Test (expected = RabbitConnectionException.class)
    public void constructorWithException() throws RabbitConnectionException, IOException {
        new Expectations() {
            {
                mockedFactory.newConnection(); result = new IOException();
            }
        };
        testConnection = new RabbitConnection(mockedCache, mockedFactory);
    }

    @Test
    public void close() throws IOException, RabbitConnectionException {
        new NonStrictExpectations() {
            {
                mockedFactory.newConnection(); times = 1; result = mockedConnection;
                mockedConnection.close(); times = 1;
                mockedCache.remove(anyString, anyInt, anyString, anyString); times = 1;
            }
        };
        testConnection = new RabbitConnection(mockedCache, mockedFactory);
        testConnection.close();
        assertTrue(testConnection.isClosed());
    }

    @Test
    public void connection() throws IOException, RabbitConnectionException {
        new NonStrictExpectations() {
            {
                mockedFactory.newConnection(); times = 1; result = mockedConnection;
            }
        };
        testConnection = new RabbitConnection(mockedCache, mockedFactory);
        assertEquals(mockedConnection, testConnection.connection());
    }

    @Test
    public void publish() throws IOException, RabbitConnectionException {
        new NonStrictExpectations() {
            {
                new MessageWrapper(MESSAGE, MessageWrapper.MessageType.MESSAGE); times = 1; result = mockedWrapper;
                mockedFactory.newConnection(); times = 1; result = mockedConnection;
                mockedConnection.createChannel(); times = 1; result = mockedChannel;
                mockedWrapper.toJson(); times = 1; result = JSON;
                mockedChannel.exchangeDeclare(EXCHANGE, EXCHANGE_TYPE, true); times = 1;
                mockedChannel.basicPublish(EXCHANGE, SUBJECT, MANDATORY,
                        (com.rabbitmq.client.AMQP.BasicProperties) withNotNull(), JSON.getBytes()); times = 1;

            }
        };
        testConnection = new RabbitConnection(mockedCache, mockedFactory);
        testConnection.publish(EXCHANGE, SUBJECT, MESSAGE, null);
    }

    @Test
    public void publishWithOptions() throws IOException, RabbitConnectionException {
        Map<Options, String> options = new HashMap<>();
        options.put(Options.ERROR_EXCHANGE, ERROR_EXCHANGE);
        options.put(Options.ERROR_SUBJECT, ERROR_SUBJECT);

        new NonStrictExpectations() {
            {
                new MessageWrapper(MESSAGE, MessageWrapper.MessageType.MESSAGE); result = mockedWrapper;
                mockedFactory.newConnection(); times = 1; result = mockedConnection;
                mockedConnection.createChannel(); times = 1; result = mockedChannel;
                mockedWrapper.toJson(); times = 1; result = JSON;
                mockedWrapper.setErrorExchange(ERROR_EXCHANGE); times = 1;
                mockedWrapper.setErrorSubject(ERROR_SUBJECT); times = 1;
                mockedChannel.basicPublish(EXCHANGE, SUBJECT, MANDATORY,
                        (com.rabbitmq.client.AMQP.BasicProperties) withNotNull(), JSON.getBytes()); times = 1;

            }
        };
        testConnection = new RabbitConnection(mockedCache, mockedFactory);
        testConnection.publish(EXCHANGE, SUBJECT, MESSAGE, options);
    }

    @Test (expected = RabbitConnectionException.class)
    public void publishWithException() throws IOException, RabbitConnectionException {
        new NonStrictExpectations() {
            {
                new MessageWrapper(MESSAGE, MessageWrapper.MessageType.MESSAGE); times = 1; result = mockedWrapper;
                mockedFactory.newConnection(); times = 1; result = mockedConnection;
                mockedConnection.createChannel(); times = 1; result = new IOException();
            }
        };
        testConnection = new RabbitConnection(mockedCache, mockedFactory);
        testConnection.publish(EXCHANGE, SUBJECT, MESSAGE, null);
    }

    @Test
    public void publishError() throws IOException, RabbitConnectionException {
        new NonStrictExpectations() {
            {
                new MessageWrapper(MESSAGE, MessageWrapper.MessageType.ERROR); times = 1; result = mockedWrapper;
                mockedFactory.newConnection(); times = 1; result = mockedConnection;
                mockedConnection.createChannel(); times = 1; result = mockedChannel;
                mockedWrapper.toJson(); times = 1; result = JSON;
                mockedChannel.basicPublish(EXCHANGE, SUBJECT, MANDATORY,
                        (com.rabbitmq.client.AMQP.BasicProperties) withNotNull(), JSON.getBytes()); times = 1;
            }
        };
        testConnection = new RabbitConnection(mockedCache, mockedFactory);
        testConnection.publishError(EXCHANGE, SUBJECT, MessageHandler.Response.REJECT, MESSAGE);
    }

    @Test
    public void consumer(final @Mocked MessageHandler mockHandler, final @Mocked RabbitConsumer mockConsumer) throws Exception {
        new NonStrictExpectations() {
            {
                mockedFactory.newConnection(); times = 1; result = mockedConnection;
                new RabbitConsumer(withInstanceOf(RabbitConnection.class), EXCHANGE, SUBJECT, QUEUE, mockHandler); result = mockConsumer;
            }
        };
        testConnection = new RabbitConnection(mockedCache, mockedFactory);
        Consumer testConsumer = testConnection.consumer(EXCHANGE, SUBJECT, QUEUE, mockHandler);
        assertNotNull(testConsumer);
    }
}
