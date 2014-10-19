package me.breidenbach.rabbitex.connection

import java.io.IOException

import com.rabbitmq.client.{Envelope, DefaultConsumer, Connection, Channel}
import me.breidenbach.rabbitex.MessageHandler
import me.breidenbach.rabbitex.connection.HandlerResponse.HandlerResponse
import me.breidenbach.rabbitex.test.BaseFixture

import org.mockito.{Matchers, ArgumentCaptor}
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.{is, nullValue, sameInstance, not}

import RabbitConsumerTest._

/**
 * Date: 10/19/14
 * Time: 2:37 PM
 * Copyright 2014 Kevin E. Breidenbach
 * @author Kevin E. Breidenbach
 */

object RabbitConsumerTest {
  val QUEUE = "queue"
  val EXCHANGE = "exchange"
  val SUBJECT = "subject"
  val ERROR_EXCHANGE = "ERROR_EXCHANGE"
  val ERROR_SUBJECT = "ERROR_SUBJECT"
  val MESSAGE_TEXT = "TEST"

  def messageWithErrorExchange(): Array[Byte] = {
    Message(MESSAGE_TEXT, Some(ERROR_EXCHANGE), Some(ERROR_SUBJECT)).toJson.getBytes
  }

  def message(): Array[Byte] = {
    Message(MESSAGE_TEXT).toJson.getBytes
  }
}

class RabbitConsumerTest extends BaseFixture {

  val mockRabbitConnection = mock[RabbitConnection]
  val mockConnection = mock[Connection]
  val mockChannel = mock[Channel]
  val mockMessageHandler = mock[MessageHandler]
  val defaultConsumerCaptor = ArgumentCaptor.forClass(classOf[DefaultConsumer])

  override def beforeEach(): Unit = {
    when(mockRabbitConnection.connection).thenReturn(mockConnection)
    when(mockConnection.createChannel()).thenReturn(mockChannel)
  }

  override def afterEach(): Unit = {
    reset(mockRabbitConnection, mockConnection, mockChannel, mockMessageHandler)
  }

  test("case class") {
    val consumer = RabbitConsumer(mockRabbitConnection, EXCHANGE, SUBJECT, QUEUE, mockMessageHandler)

    assertThat(consumer, not(nullValue()))
    verify(mockChannel, times(1)).queueDeclare(QUEUE, true, false, false, null)
    verify(mockChannel, times(1)).queueBind(QUEUE, EXCHANGE, SUBJECT)
  }

  test("case class fail") {
    when(mockConnection.createChannel()).thenThrow(new IOException("Error"))

    intercept[RabbitConnectionException] {
      RabbitConsumer(mockRabbitConnection, EXCHANGE, SUBJECT, QUEUE, mockMessageHandler)
    }
  }

  test("case class bind fail") {
    doThrow(new IOException("ERROR")).when(mockChannel).queueBind(QUEUE, EXCHANGE, SUBJECT)

    intercept[RabbitConnectionException] {
      RabbitConsumer(mockRabbitConnection, EXCHANGE, SUBJECT, QUEUE, mockMessageHandler)
    }
  }

  test("start") {
    RabbitConsumer(mockRabbitConnection, EXCHANGE, SUBJECT, QUEUE, mockMessageHandler).start()

    verify(mockChannel, times(1)).basicConsume(Matchers.eq(QUEUE), Matchers.eq(false), defaultConsumerCaptor.capture())
    assertThat(defaultConsumerCaptor.getValue.getChannel, is(sameInstance(mockChannel)))
  }

  test("handle message with ACK") {
    when(mockMessageHandler.handleMessage(MESSAGE_TEXT)).thenReturn(MessageHandler.Response.ACK)

    val mockEnvelope = mock[Envelope]
    val consumer = RabbitConsumer(mockRabbitConnection, EXCHANGE, SUBJECT, QUEUE, mockMessageHandler)
    consumer.handleMessage(mockEnvelope, message())
    verify(mockChannel, times(1)).basicAck(anyLong, Matchers.eq(false))
  }

  test("handle message with REJECT") {
    when(mockMessageHandler.handleMessage(MESSAGE_TEXT)).thenReturn(MessageHandler.Response.REJECT)

    val mockEnvelope = mock[Envelope]
    val consumer = RabbitConsumer(mockRabbitConnection, EXCHANGE, SUBJECT, QUEUE, mockMessageHandler)
    consumer.handleMessage(mockEnvelope, messageWithErrorExchange())

    verify(mockChannel, times(1)).basicNack(anyLong, Matchers.eq(false), Matchers.eq(false))
    verify(mockRabbitConnection, times(1)).publishError(ERROR_EXCHANGE, ERROR_SUBJECT, HandlerResponse.REJECT, MESSAGE_TEXT)
  }

  test("handle message with REQUEUE") {
    when(mockMessageHandler.handleMessage(MESSAGE_TEXT)).thenReturn(MessageHandler.Response.REQUEUE)

    val mockEnvelope = mock[Envelope]
    val consumer = RabbitConsumer(mockRabbitConnection, EXCHANGE, SUBJECT, QUEUE, mockMessageHandler)
    consumer.handleMessage(mockEnvelope, messageWithErrorExchange())

    verify(mockChannel, times(1)).basicNack(anyLong, Matchers.eq(false), Matchers.eq(true))
    verify(mockRabbitConnection, times(1)).publishError(ERROR_EXCHANGE, ERROR_SUBJECT, HandlerResponse.REQUEUE, MESSAGE_TEXT)
  }

  test("handle error message with REJECT and ensure no error message published") {
    when(mockMessageHandler.handleMessage("ERROR")).thenReturn(MessageHandler.Response.REQUEUE)

    val mockEnvelope = mock[Envelope]
    val consumer = RabbitConsumer(mockRabbitConnection, EXCHANGE, SUBJECT, QUEUE, mockMessageHandler)
    consumer.handleMessage(mockEnvelope, ErrorMessage("ERROR", HandlerResponse.REJECT).toJson.getBytes)

    verify(mockChannel, times(1)).basicNack(anyLong(), Matchers.eq(false), Matchers.eq(true))
    verify(mockRabbitConnection, never).publishError(anyString, anyString,
      any(classOf[HandlerResponse]), anyString)
  }

  test("default consumer") {
    when(mockMessageHandler.handleMessage(MESSAGE_TEXT)).thenReturn(MessageHandler.Response.ACK)
    val mockEnvelope = mock[Envelope]
    val consumer = RabbitConsumer(mockRabbitConnection, EXCHANGE, SUBJECT, QUEUE, mockMessageHandler)
    val defaultConsumer = consumer.defaultConsumer()

    defaultConsumer.handleDelivery("consumerTag", mockEnvelope, null, message())

    verify(mockMessageHandler, times(1)).handleMessage(MESSAGE_TEXT)
  }
}