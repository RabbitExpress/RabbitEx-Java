package me.breidenbach.rabbitex.connection;

import org.junit.Test;
import scala.Option;
import scala.Some;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * User: Kevin E. Breidenbach
 * Date: 11/1/13
 * Time: 10:15 PM
 * Copyright 2013 Kevin E. Breidenbach
 */
 public class MessageWrapperTest {
    private static final Option<String> NONE = Option.apply(null);

    private static final String MESSAGE = "{\"test_message\":\"TEST\"}";
    private static final String EXPECTED =
            "{\"message\":\"{\\\"test_message\\\":\\\"TEST\\\"}\",\"messageType\":\"MESSAGE\"}";
    private static final String EXPECTED_WITH_ERROR_EXCHANGE =
            "{\"message\":\"{\\\"test_message\\\":\\\"TEST\\\"}\",\"messageType\":\"MESSAGE\",\"errorExchange\":\"ErrorExchange\",\"errorSubject\":\"ErrorSubject\"}";
    private static final String EXPECTED_ERROR =
            "{\"message\":\"{\\\"test_message\\\":\\\"TEST\\\"}\",\"messageType\":\"ERROR\",\"errorAction\":\"REJECT\"}";

    private MessageWrapper testWrapper;

    @Test
    public void toJson() {
        testWrapper = new Message(MESSAGE, NONE, NONE);

        String json = testWrapper.toJson();
        assertThat(EXPECTED, is(json));
    }

    @Test
    public void fromJson() {
        MessageWrapper wrapper = MessageWrapper$.MODULE$.fromJson(EXPECTED);
        assertThat(MessageType$.MODULE$.MESSAGE(), is(wrapper.messageType()));
        assertThat(MESSAGE, is(wrapper.message()));
    }

    @Test
    public void toJsonWithErrorExchange() {
        testWrapper = new Message(MESSAGE, new Some<>("ErrorExchange"), new Some<>("ErrorSubject"));
        String json = testWrapper.toJson();
        assertThat(EXPECTED_WITH_ERROR_EXCHANGE, is(json));
    }
    @Test
    public void toJsonError() {
        testWrapper = new ErrorMessage(MESSAGE, HandlerResponse.REJECT());
        String json = testWrapper.toJson();
        assertThat(EXPECTED_ERROR, is(json));
    }

    @Test
    public void fromJsonError() {
        Message wrapper = (Message) MessageWrapper$.MODULE$.fromJson(EXPECTED_WITH_ERROR_EXCHANGE);
        assertThat(MessageType.MESSAGE(), is(wrapper.messageType()));
        assertThat(MESSAGE, is(wrapper.message()));
        assertThat("ErrorExchange", is(wrapper.errorExchange().get()));
        assertThat("ErrorSubject", is(wrapper.errorSubject().get()));
    }
}
