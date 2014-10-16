package me.breidenbach.rabbitex.connection;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/1/13
 * Time: 8:58 PM
 * Copyright 2013 Kevin E. Breidenbach
 */
 public class RabbitConnectionExceptionTest {
    @Test
    public void getMessage() {
        final String message = "Test";
        assertEquals(message, new RabbitConnectionException(message, null).getMessage());
    }
}
