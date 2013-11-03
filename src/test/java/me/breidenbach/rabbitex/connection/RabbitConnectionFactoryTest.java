package me.breidenbach.rabbitex.connection;

import mockit.Expectations;
import mockit.Mocked;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/1/13
 * Time: 7:33 PM
 * © 2013 Kevin E. Breidenbach
 */
public class RabbitConnectionFactoryTest {
    private final String hostname = "test.com";
    private final int port = 1;
    private final String virtualHost = "";
    private final String username = "";

    private RabbitConnectionFactory factory;

    @Mocked(stubOutClassInitialization = true) RabbitConnection mockedConnection;


    @Before
    public void setUp() {
        factory = new RabbitConnectionFactory();
    }

    @Test
    public void rabbitConnectionNew(final @Mocked RabbitConnectionCache cache) throws RabbitConnectionException {

        new Expectations() {
            {
                cache.retrieve(hostname, port, virtualHost, username); result = null;
                cache.cache(hostname, port, virtualHost, username, withInstanceOf(RabbitConnection.class));
            }
        };

        assertNotNull(factory.rabbitConnection(hostname, port));
    }

    @Test
    public void rabbitConnectionCached(final @Mocked RabbitConnectionCache cache) throws RabbitConnectionException {

        new Expectations() {
            {
                cache.retrieve(hostname, port, virtualHost, username); result = mockedConnection;
            }
        };

        factory.rabbitConnection(hostname, port);
    }
}
