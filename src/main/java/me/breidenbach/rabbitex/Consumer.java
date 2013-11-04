package me.breidenbach.rabbitex;


import java.io.IOException;

/**
 * Date: 11/1/13
 * Time: 3:56 PM
 * Copyright 2013 Kevin E. Breidenbach
 * @author Kevin E. Breidenbach
 */
public interface Consumer {
    /*
     * Starts the consumer
     *
     */
    public void start() throws IOException;
}
