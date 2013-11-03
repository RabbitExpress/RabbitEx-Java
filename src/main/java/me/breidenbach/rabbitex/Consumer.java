package me.breidenbach.rabbitex;


import java.io.IOException;

/**
 * Date: 11/1/13
 * Time: 3:56 PM
 * © 2013 Kevin E. Breidenbach
 * @author Kevin E. Breidenbach
 */
public interface Consumer {
    /*
     * starts the consumer
     */
    void start() throws IOException;
}
