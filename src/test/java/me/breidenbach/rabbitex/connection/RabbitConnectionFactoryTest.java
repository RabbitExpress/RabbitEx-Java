package me.breidenbach.rabbitex.connection;

import com.rabbitmq.client.ConnectionFactory;
import me.breidenbach.rabbitex.RabbitEx;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.is;


/**
 * User: Kevin E. Breidenbach
 * Date: 11/1/13
 * Time: 7:33 PM
 * Copyright 2013 Kevin E. Breidenbach
 */
public class RabbitConnectionFactoryTest {

    @Tested
    private RabbitConnectionFactory factory;

    @Mocked(stubOutClassInitialization = true)
    private RabbitConnection$ mockRabbitConnection$;

    @Mocked
    private RabbitEx mockRabbitConnection;


    @Before
    public void setUp() {
        factory = new RabbitConnectionFactory();
        ReflectionTestUtils.setField(factory, "rabbitConnection", mockRabbitConnection$);

        new Expectations() {
            {
                mockRabbitConnection$.newConnection(anyString, anyInt, anyString,
                        anyString, anyString, withInstanceOf(ConnectionFactory.class)); result = mockRabbitConnection;
            }
        };
    }

    @Test
    public void rabbitConnectionNew() throws RabbitConnectionException {
        final String hostname = "test.com";
        final int port = 1;
        final String virtualHost = "";
        final String username = "";
        final RabbitEx connection = factory.rabbitConnection(hostname, port, virtualHost, username, null);
        assertThat(connection, is(sameInstance(mockRabbitConnection)));
    }
}
