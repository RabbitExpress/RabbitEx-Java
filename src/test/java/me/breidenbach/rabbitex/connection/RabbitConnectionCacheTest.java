package me.breidenbach.rabbitex.connection;

import mockit.Mocked;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/1/13
 * Time: 6:47 PM
 * © 2013 Kevin E. Breidenbach
 */
public class RabbitConnectionCacheTest {
    private final String hostname = "test.com";
    private final int port = 1;
    private final String virtualHost = "test";
    private final String username = "user";

    private RabbitConnectionCache testCache;

    @Mocked RabbitConnection mockedConnection;

    @Before
    public void setUp() {
        testCache = new RabbitConnectionCache();
        testCache.cache(hostname, port, virtualHost, username, mockedConnection);
    }

    @Test
    public void testRetrieve() throws Exception {
        assertSame(mockedConnection, testCache.retrieve(hostname, port, virtualHost, username));
    }

    @Test
    public void testRemove() throws Exception {
        testCache.remove(hostname, port, virtualHost, username);
        assertNull(testCache.retrieve(hostname, port, virtualHost, username));
    }
}
