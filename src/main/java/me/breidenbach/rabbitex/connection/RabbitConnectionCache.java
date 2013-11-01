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

    void cache(String listenAddress, int listenPort, String virtualHost,
                        String username, RabbitConnection connection) {
        connections.put(createKey(listenAddress, listenPort, virtualHost, username), connection);
    }

    RabbitConnection retrieve(String listenAddress, int listenPort, String virtualHost,
                              String username) {
        return connections.get(createKey(listenAddress, listenPort, virtualHost, username));
    }

    void remove(String listenAddress, int listenPort, String virtualHost,
                String username) {
        connections.remove(createKey(listenAddress, listenPort, virtualHost, username));
    }


    private String createKey(String listenAddress, int listenPort, String virtualHost,
                             String username) {
        return listenAddress + listenPort + virtualHost + username;
    }
}
