package me.breidenbach.rabbitex.connection;

/**
 * Date: 11/1/13
 * Time: 5:09 PM
 * Copyright 2013 Kevin E. Breidenbach
 * @author Kevin E. Breidenbach
 */
public class RabbitConnectionException extends Exception {
    RabbitConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
