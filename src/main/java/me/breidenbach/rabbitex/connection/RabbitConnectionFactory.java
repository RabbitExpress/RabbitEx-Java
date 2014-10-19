package me.breidenbach.rabbitex.connection;

import com.rabbitmq.client.ConnectionFactory;
import me.breidenbach.rabbitex.RabbitEx;
import org.springframework.stereotype.Service;

/**
 * Date: 10/15/14
 * Time: 11:01 PM
 * Copyright 2014 Kevin E. Breidenbach
 *
 * @author Kevin E. Breidenbach
 */
@Service("rabbitConnectionFactory")
public class RabbitConnectionFactory {
    // Needed because Java can't handle implicit scala parameters
    private ConnectionFactory FACTORY = new ConnectionFactory();

    private RabbitConnection$ rabbitConnection = RabbitConnection$.MODULE$;

    public RabbitEx rabbitConnection(String listenAddress, int listenPort) throws RabbitConnectionException {
        return rabbitConnection (listenAddress, listenPort, "", "", "");
    }

    public RabbitEx rabbitConnection(String listenAddress, int listenPort,
                                     String virtualHost, String username, String password) throws RabbitConnectionException {
        return rabbitConnection.newConnection(listenAddress, listenPort, virtualHost, username, password, FACTORY);
    }
}
