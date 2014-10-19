package me.breidenbach.rabbitex.connection

import me.breidenbach.rabbitex.MessageHandler
import me.breidenbach.rabbitex.test.BaseFixture
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.is

/**
 * Date: 10/19/14
 * Time: 6:16 PM
 * Copyright 2014 Kevin E. Breidenbach
 * @author Kevin E. Breidenbach
 */
class HandlerResponseTest extends BaseFixture {

  test("to java") {
    assertThat(HandlerResponse.toJava(HandlerResponse.ACK), is(MessageHandler.Response.ACK))
    assertThat(HandlerResponse.toJava(HandlerResponse.REJECT), is(MessageHandler.Response.REJECT))
    assertThat(HandlerResponse.toJava(HandlerResponse.REQUEUE), is(MessageHandler.Response.REQUEUE))
  }

  test("from java") {
    assertThat(HandlerResponse.fromJava(MessageHandler.Response.ACK), is(HandlerResponse.ACK))
    assertThat(HandlerResponse.fromJava(MessageHandler.Response.REJECT), is(HandlerResponse.REJECT))
    assertThat(HandlerResponse.fromJava(MessageHandler.Response.REQUEUE), is(HandlerResponse.REQUEUE))
  }

}
