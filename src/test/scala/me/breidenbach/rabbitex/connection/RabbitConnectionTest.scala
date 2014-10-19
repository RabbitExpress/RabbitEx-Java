package me.breidenbach.rabbitex.connection

import java.io.IOException

import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.{Connection, Channel}
import com.rabbitmq.client.ConnectionFactory
import me.breidenbach.rabbitex.MessageHandler
import me.breidenbach.rabbitex.test.BaseFixture
import org.mockito.{Matchers, ArgumentCaptor}
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.{is, nullValue, sameInstance, not}
import RabbitConnectionTest._


/**
 * Date: 10/16/14
 * Time: 10:39 AM
 * Copyright 2014 Kevin E. Breidenbach
 * @author Kevin E. Breidenbach
 */

object RabbitConnectionTest {
  val EXCHANGE = "exchange"
  val SUBJECT = "subject"
  val QUEUE = "queue"
  val MESSAGE = "message"
  val ERROR_EXCHANGE = "ERROR_EXCHANGE"
  val ERROR_SUBJECT = "ERROR_SUBJECT"
  val JSON = "{}"
  val EXCHANGE_TYPE = "topic"
  val HOST = "testhost"
  val PORT = 12000
  val VIRTUAL_HOST = "virtualHost"
  val USERNAME = "username"
  val PASSWORD = "password"
}

class RabbitConnectionTest extends BaseFixture {

  val mockConnectionFactory = mock[ConnectionFactory]
  val mockConnection = mock[Connection]
  val mockChannel = mock[Channel]
  val mockHandler = mock[MessageHandler]
  val bytesCaptor = ArgumentCaptor.forClass(classOf[Array[Byte]])

  override def beforeEach(): Unit = {
    when(mockConnectionFactory.newConnection).thenReturn(mockConnection)
  }

  override def afterEach(): Unit = {
    reset(mockConnectionFactory, mockConnection, mockChannel, mockHandler)
  }

  test("case class") {
    val rabbitConnection = RabbitConnection(HOST, PORT, VIRTUAL_HOST, USERNAME, PASSWORD, mockConnectionFactory)

    assertThat(rabbitConnection.factory, is(mockConnectionFactory))
    assertThat(rabbitConnection.connection, is(mockConnection))
    assertThat(rabbitConnection.key, is(HOST + PORT + VIRTUAL_HOST + USERNAME))
    assertThat(rabbitConnection.closed, is(false))
    verify(mockConnectionFactory, times(1)).newConnection
  }

  test("case class fail") {
    when(mockConnectionFactory.newConnection).thenThrow(new IOException("Error"))
    intercept[RabbitConnectionException] {
      RabbitConnection(HOST, PORT, VIRTUAL_HOST, USERNAME, PASSWORD, mockConnectionFactory)
    }
  }

  test("case class no factory") {
    val rabbitConnection = RabbitConnection(HOST, PORT, VIRTUAL_HOST, USERNAME, PASSWORD)

    assertThat(rabbitConnection.connection, nullValue)
    assertThat(rabbitConnection.factory, nullValue)
    assertThat(rabbitConnection.key, is(HOST + PORT + VIRTUAL_HOST + USERNAME))
    assertThat(rabbitConnection.closed, is(false))
    verify(mockConnectionFactory, never).newConnection()
  }

  test("publish") {
    when(mockConnection.createChannel).thenReturn(mockChannel)

    val rabbitConnection = RabbitConnection(HOST, PORT, VIRTUAL_HOST, USERNAME, PASSWORD, mockConnectionFactory)
    val messageWrapper = Message(MESSAGE)

    rabbitConnection.publish(EXCHANGE, SUBJECT, MESSAGE)

    verify(mockChannel, times(1)).exchangeDeclare(EXCHANGE, EXCHANGE_TYPE, true)
    verify(mockChannel, times(1)).basicPublish(Matchers.eq(EXCHANGE), Matchers.eq(SUBJECT),
      any[BasicProperties], bytesCaptor.capture)
    assertThat(new String(bytesCaptor.getValue.map(_.toChar)), is(messageWrapper.toJson))
  }

  test("publish fail") {
    when(mockConnection.createChannel).thenThrow(new IOException("Error"))

    val rabbitConnection = RabbitConnection(HOST, PORT, VIRTUAL_HOST, USERNAME, PASSWORD, mockConnectionFactory)

    intercept[RabbitConnectionException] {
      rabbitConnection.publish(EXCHANGE, SUBJECT, MESSAGE)
    }
  }

  test("publish error") {
    when(mockConnectionFactory.newConnection).thenReturn(mockConnection)
    when(mockConnection.createChannel).thenReturn(mockChannel)
    val rabbitConnection = RabbitConnection(HOST, PORT, VIRTUAL_HOST, USERNAME, PASSWORD, mockConnectionFactory)

    rabbitConnection.publishError(ERROR_EXCHANGE, ERROR_SUBJECT, HandlerResponse.REJECT, MESSAGE)
    verify(mockChannel, times(1)).basicPublish(Matchers.eq(ERROR_EXCHANGE), Matchers.eq(ERROR_SUBJECT),
      any[BasicProperties], bytesCaptor.capture)

    val errorMessage =  MessageWrapper.fromJson(new String(bytesCaptor.getValue.map(_.toChar)))
    assertThat(errorMessage.messageType, is(MessageType.ERROR))
    assertThat(errorMessage.message, is(MESSAGE))
  }

  test("consume") {
    when(mockConnection.createChannel).thenReturn(mockChannel)

    val rabbitConnection = RabbitConnection(HOST, PORT, VIRTUAL_HOST, USERNAME, PASSWORD, mockConnectionFactory)
    val consumer = rabbitConnection.consumer(EXCHANGE, SUBJECT, QUEUE, mockHandler)

    assertResult(false)(consumer == null)

    verify(mockChannel, times(1)).queueDeclare(QUEUE, true, false, false, null)
    verify(mockChannel, times(1)).queueBind(QUEUE, EXCHANGE, SUBJECT)
  }

  test("close") {
    val rabbitConnection = RabbitConnection(HOST, PORT, VIRTUAL_HOST, USERNAME, PASSWORD, mockConnectionFactory)

    rabbitConnection.close()

    assertThat(rabbitConnection.closed, is(true))
  }

  test("new connection") {
    val rabbitConnection = RabbitConnection.newConnection(HOST, PORT, VIRTUAL_HOST, USERNAME, PASSWORD)(mockConnectionFactory)
    val rabbitConnection2 = RabbitConnection.newConnection(HOST, PORT, VIRTUAL_HOST, USERNAME, PASSWORD)(mockConnectionFactory)

    rabbitConnection2 match {
      case rc: RabbitConnection =>
        RabbitConnection.remove(rc)
    }

    val rabbitConnection3 = RabbitConnection.newConnection(HOST, PORT, VIRTUAL_HOST, USERNAME, PASSWORD)(mockConnectionFactory)

    assertThat(rabbitConnection, is(sameInstance(rabbitConnection2)))
    assertThat(rabbitConnection, not(sameInstance(rabbitConnection3)))
  }
}