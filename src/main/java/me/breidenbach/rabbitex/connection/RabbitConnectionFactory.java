package me.breidenbach.rabbitex.connection;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/1/13
 * Time: 4:08 PM
 * © 2013 Kevin E. Breidenbach
 */
public class RabbitConnectionFactory {

    private RabbitConnectionCache cache = new RabbitConnectionCache();

    public RabbitConnection rabbitConnection(String listenAddress, int listenPort) throws RabbitConnectionException {
        return rabbitConnection (listenAddress, listenPort, "", "", "");
    }

    public RabbitConnection rabbitConnection(String listenAddress, int listenPort, String virtualHost,
                                             String username, String password) throws RabbitConnectionException {

        RabbitConnection connection = cache.retrieve(listenAddress, listenPort, virtualHost, username);
        if (connection == null || connection.isClosed()) {
            connection = new RabbitConnection(cache, listenAddress, listenPort, virtualHost, username, password);
            cache.cache(listenAddress, listenPort, virtualHost, username, connection);
        }
        return connection;
    }
}
