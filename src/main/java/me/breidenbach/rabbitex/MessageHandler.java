package me.breidenbach.rabbitex;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/2/13
 * Time: 5:58 PM
 * © 2013 Kevin E. Breidenbach
 */
public interface MessageHandler {
    static enum Response {
        REJECT,
        REQUEUE,
        ACK
    }

    Response handleMessage(final String message);
}
