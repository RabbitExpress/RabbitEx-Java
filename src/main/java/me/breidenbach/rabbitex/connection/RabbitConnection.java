package me.breidenbach.rabbitex.connection;

import com.rabbitmq.client.Connection;
import me.breidenbach.rabbitex.Consumer;
import me.breidenbach.rabbitex.Options;
import me.breidenbach.rabbitex.RabbitEx;

import java.io.IOException;
import java.util.Map;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/1/13
 * Time: 3:59 PM
 * © 2013 Kevin E. Breidenbach
 */
public class RabbitConnection implements RabbitEx {

    private final RabbitConnectionCache cache;
    private final String listenAddress;
    private final int listenPort;
    private final String virtualHost;
    private final String username;
    private final String password;

    private final Connection connection; //the underlying RabbitConnection

    private boolean closed = false;

    RabbitConnection(RabbitConnectionCache cache, String listenAddress, int listenPort, String virtualHost,
                     String username, String password) throws RabbitConnectionException {
        this.cache = cache;
        this.listenAddress = listenAddress;
        this.listenPort = listenPort;
        this.virtualHost = virtualHost;
        this.username = username;
        this.password = password;
        this.connection = prepareRabbitConnection();
    }

    @Override
    public void close() throws IOException {
        connection.close();
        cache.remove(listenAddress, listenPort, virtualHost, username);
        closed = true;
    }

    @Override
    public void publish(String exchange, String subject, Map<Options, String> options) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Consumer consumer(String exchange, String subject, String queue) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isClosed() {
        return closed;
    }

    private Connection prepareRabbitConnection() {
        return null;
    }


}
