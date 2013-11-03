package me.breidenbach.rabbitex.connection;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/1/13
 * Time: 6:13 PM
 * © 2013 Kevin E. Breidenbach
 */
class RabbitConnectionCache {

    private final Map<String, RabbitConnection> connections = new WeakHashMap<>();

    RabbitConnectionCache() { }

    void cache(final String listenAddress, final int listenPort, final String virtualHost,
                        final String username, final RabbitConnection connection) {
        connections.put(createKey(listenAddress, listenPort, virtualHost, username), connection);
    }

    RabbitConnection retrieve(final String listenAddress, final int listenPort,
                              final String virtualHost, final String username) {
        return connections.get(createKey(listenAddress, listenPort, virtualHost, username));
    }

    void remove(final String listenAddress, final int listenPort,
                final String virtualHost, final String username) {
        connections.remove(createKey(listenAddress, listenPort, virtualHost, username));
    }


    private String createKey(final String listenAddress, final int listenPort,
                             final String virtualHost, final String username) {
        return listenAddress + listenPort + virtualHost + username;
    }
}
