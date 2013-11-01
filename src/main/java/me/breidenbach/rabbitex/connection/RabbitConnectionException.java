package me.breidenbach.rabbitex.connection;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/1/13
 * Time: 5:09 PM
 * © 2013 Kevin E. Breidenbach
 */
public class RabbitConnectionException extends Exception {
    RabbitConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
