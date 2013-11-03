package me.breidenbach.rabbitex.connection;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/1/13
 * Time: 10:15 PM
 * © 2013 Kevin E. Breidenbach
 */
public class MessageWrapperTest {
    private static final String MESSAGE = "{\"test_message\":\"TEST\"}";
    private static final String EXPECTED =
            "{\"message\":\"{\\\"test_message\\\":\\\"TEST\\\"}\",\"messageType\":\"MESSAGE\"}";
    private static final String EXPECTED_ERROR =
            "{\"message\":\"{\\\"test_message\\\":\\\"TEST\\\"}\",\"messageType\":\"ERROR\",\"errorExchange\":\"ErrorExchange\",\"errorSubject\":\"ErrorSubject\",\"errorAction\":\"REJECT\"}";

    private MessageWrapper testWrapper;

    @Test
    public void toJson() {
        testWrapper = new MessageWrapper(MESSAGE);

        String json = testWrapper.toJson();
        assertEquals(EXPECTED, json);
    }

    @Test
    public void fromJson() {
        MessageWrapper wrapper = MessageWrapper.fromJson(EXPECTED);
        assertEquals(MessageWrapper.MessageType.MESSAGE, wrapper.getMessageType());
        assertEquals(MESSAGE, wrapper.getMessage());
    }

    @Test
    public void toJsonError() {
        testWrapper = new MessageWrapper(MESSAGE, MessageWrapper.MessageType.ERROR);
        testWrapper.setErrorExchange("ErrorExchange");
        testWrapper.setErrorSubject("ErrorSubject");
        testWrapper.setErrorAction(MessageWrapper.ErrorAction.REJECT);
        String json = testWrapper.toJson();
        assertEquals(EXPECTED_ERROR, json);
    }

    @Test
    public void fromJsonError() {
        MessageWrapper wrapper = MessageWrapper.fromJson(EXPECTED_ERROR);
        assertEquals(MessageWrapper.MessageType.ERROR, wrapper.getMessageType());
        assertEquals(MESSAGE, wrapper.getMessage());
        assertEquals("ErrorExchange", wrapper.getErrorExchange());
        assertEquals("ErrorSubject", wrapper.getErrorSubject());
        assertEquals(MessageWrapper.ErrorAction.REJECT, wrapper.getErrorAction());
    }
}
