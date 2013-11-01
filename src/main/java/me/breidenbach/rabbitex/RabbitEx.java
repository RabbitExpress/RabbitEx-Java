package me.breidenbach.rabbitex;

import java.io.Closeable;
import java.util.Map;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/1/13
 * Time: 3:32 PM
 * © 2013 Kevin E. Breidenbach
 */

public interface RabbitEx extends Closeable {

    void publish(String exchange, String subject, Map<Options, String> options);

    Consumer consumer(String exchange, String subject, String queue);

}

