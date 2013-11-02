package me.breidenbach.rabbitex.connection;


import me.breidenbach.rabbitex.RabbitEx;
import org.springframework.stereotype.Component;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/1/13
 * Time: 4:08 PM
 * © 2013 Kevin E. Breidenbach
 */
@Component
public class RabbitConnectionFactory {

    private RabbitConnectionCache cache = new RabbitConnectionCache();

    public RabbitEx rabbitConnection(String host, int port) throws RabbitConnectionException {
        return rabbitConnection (host, port, "", "", "");
    }

    public RabbitEx rabbitConnection(String host, int port, String virtualHost,
                                             String username, String password) throws RabbitConnectionException {

        RabbitConnection connection = cache.retrieve(host, port, virtualHost, username);
        if (connection == null || connection.isClosed()) {
            connection = new RabbitConnection(cache, host, port, virtualHost, username, password);
            cache.cache(host, port, virtualHost, username, connection);
        }
        return connection;
    }
}
