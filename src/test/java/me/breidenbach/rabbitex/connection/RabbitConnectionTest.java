package me.breidenbach.rabbitex.connection;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/2/13
 * Time: 12:13 PM
 * © 2013 Kevin E. Breidenbach
 */
public class RabbitConnectionTest {
    private final String hostname = "test.com";
    private final int port = 1;
    private final String virtualHost = "test";
    private final String username = "username";
    private final String password = "password";

    private RabbitConnection testConnection;

    @Mocked ConnectionFactory mockedFactory;
    @Mocked RabbitConnectionCache mockedCache;
    @Mocked Connection mockedConnection;


    @Test
    public void constructor() throws RabbitConnectionException, IOException {
        new Expectations() {
            {

                mockedFactory.newConnection(); result = mockedConnection;
            }
        };
        testConnection = new RabbitConnection(mockedCache, mockedFactory);
    }

    @Test
    public void close() throws Exception {
        new NonStrictExpectations() {
            {
                mockedFactory.newConnection(); result = mockedConnection;
                mockedConnection.close();
                mockedCache.remove(hostname, port, virtualHost, username);
            }
        };
        testConnection = new RabbitConnection(mockedCache, mockedFactory);
        testConnection.close();
        assertTrue(testConnection.isClosed());
    }

    @Test
    public void publish() throws Exception {
        new NonStrictExpectations() {
            {
                mockedFactory.newConnection(); result = mockedConnection;
            }
        };
        testConnection = new RabbitConnection(mockedCache, mockedFactory);


    }

    @Test
    public void consumer() throws Exception {

    }

    @Test
    public void publish_error() throws Exception {

    }
}
