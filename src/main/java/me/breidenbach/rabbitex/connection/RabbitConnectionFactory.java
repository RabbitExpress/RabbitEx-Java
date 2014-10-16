package me.breidenbach.rabbitex.connection;

import me.breidenbach.rabbitex.RabbitEx;

/**
 * Date: 10/15/14
 * Time: 11:01 PM
 * Copyright 2014 Kevin E. Breidenbach
 *
 * @author Kevin E. Breidenbach
 */
public class RabbitConnectionFactory {
    public RabbitEx rabbitConnection(String listenAddress, int listenPort) throws RabbitConnectionException {
        return rabbitConnection (listenAddress, listenPort, "", "", "");
    }

    public RabbitEx rabbitConnection(String listenAddress, int listenPort, String virtualHost,
                                                             String username, String password) throws RabbitConnectionException {
        return RabbitConnection$.MODULE$.rabbitConnection(listenAddress, listenPort, virtualHost, username, password);
    }
}
