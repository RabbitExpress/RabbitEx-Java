package me.breidenbach.rabbitex.connection;


import com.rabbitmq.client.ConnectionFactory;
import me.breidenbach.rabbitex.RabbitEx;
import org.springframework.stereotype.Component;

/**
 * Date: 11/1/13
 * Time: 4:08 PM
 * © 2013 Kevin E. Breidenbach
 * @author Kevin E. Breidenbach
 */
@Component
public class RabbitConnectionFactory {

    private final RabbitConnectionCache cache = new RabbitConnectionCache();

    /*
     * Creates a connection to a RabbitMQ instance and passes back the RabbitEx instance that can be used
     * for all message handling
     * @param host the hostname of the RabbitMQ service
     * @param port the port number rabbit is running on
     */
    public RabbitEx rabbitConnection(final String host, final int port) throws RabbitConnectionException {
        return rabbitConnection (host, port, "", "", "");
    }

    /*
     * Creates a connection to a RabbitMQ instance and passes back the RabbitEx instance that can be used
     * for all message handling
     * @param host the hostname of the RabbitMQ service
     * @param port the port number rabbit is running on
     * @param virtualHost the name of the virtual host to use (e.g. test)
     * @param username the username to connect to the virtual host
     * @param password the password to authenticate with
     */
    public RabbitEx rabbitConnection(final String host, final int port, final String virtualHost,
                                     final String username, final String password) throws RabbitConnectionException {

        RabbitConnection connection = cache.retrieve(host, port, virtualHost, username);
        if (connection == null || connection.isClosed()) {
            ConnectionFactory factory = createConnectionFactory(host, port, virtualHost, username, password);
            connection = new RabbitConnection(cache, factory);
            cache.cache(host, port, virtualHost, username, connection);
        }
        return connection;
    }

    private ConnectionFactory createConnectionFactory(final String host, final int port, final String virtualHost,
                                                      final String username, final String password) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        if (virtualHost != null && !virtualHost.isEmpty()) {
            factory.setVirtualHost(virtualHost);
        }
        if (username != null && !username.isEmpty()) {
            factory.setUsername(username);
        }
        if (password != null && !password.isEmpty()) {
            factory.setPassword(password);
        }
        return factory;
    }
}
