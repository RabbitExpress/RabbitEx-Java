package me.breidenbach.rabbitex;

import java.io.IOException;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/1/13
 * Time: 3:56 PM
 * © 2013 Kevin E. Breidenbach
 */
public interface Consumer {
    void start() throws IOException;
    void stop();
}
