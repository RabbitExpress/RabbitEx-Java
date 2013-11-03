package me.breidenbach.rabbitex;

/**
 * Date: 11/2/13
 * Time: 5:58 PM
 * © 2013 Kevin E. Breidenbach
 * @author Kevin E. Breidenbach
 */
public interface MessageHandler {
    static enum Response {
        REJECT,
        REQUEUE,
        ACK
    }

    /*
     * The method to be implemented by the handler to process the message
     * @param message is the String message that will be delivered by the consumer
     */
    Response handleMessage(final String message);
}
