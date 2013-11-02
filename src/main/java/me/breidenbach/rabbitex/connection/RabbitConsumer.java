package me.breidenbach.rabbitex.connection;

import me.breidenbach.rabbitex.Consumer;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/1/13
 * Time: 9:17 PM
 * © 2013 Kevin E. Breidenbach
 */
public class RabbitConsumer implements Consumer {
    private final RabbitConnection connection;

    RabbitConsumer(final RabbitConnection connection) {
        this.connection = connection;
    }

    @Override
    public void start() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void stop() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
