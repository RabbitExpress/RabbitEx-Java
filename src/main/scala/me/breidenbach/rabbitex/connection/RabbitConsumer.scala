package me.breidenbach.rabbitex.connection

import java.io.IOException

import com.rabbitmq.client.impl.AMQBasicProperties
import com.rabbitmq.client.{Envelope, DefaultConsumer}
import me.breidenbach.rabbitex.MessageHandler.Response
import me.breidenbach.rabbitex.{MessageHandler, Consumer}

/**
 * Date: 10/12/14
 * Time: 9:35 PM
 * Copyright 2014 Kevin E. Breidenbach
 * @author Kevin E. Breidenbach
 */
private[connection] class RabbitConsumer(connection: RabbitConnection, exchange: String, subject: String,
                                         queue: String, handler: MessageHandler) extends Consumer {
  val DURABLE = true
  val AUTO_DELETE = false
  val EXCLUSIVE = false
  val AUTO_ACK = false

  val channel = try {
    connection.connection.createChannel()
  } catch {
    case e: IOException => throw new RabbitConnectionException(e.getMessage, e)
  }

  try {
    channel.queueDeclare(queue, DURABLE, EXCLUSIVE, AUTO_DELETE, null)
    channel.queueBind(queue, exchange, subject)
  } catch {
    case e: IOException => throw new RabbitConnectionException("Unable to create queue: " + e.getMessage, e)
  }

  def start(): Unit = {
    channel.basicConsume(queue, AUTO_ACK, defaultConsumer())
  }

  private def defaultConsumer(): DefaultConsumer = {
    new DefaultConsumer(channel) {
      def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQBasicProperties, body: Array[Byte]): Unit = {
        handler(envelope, body)
      }
    }
  }

  private[connection] def handler(envelope: Envelope, body: Array[Byte]) {
    val json = new String(body)
    val deliveryTag = envelope.getDeliveryTag
    val wrapper: MessageWrapper = MessageWrapper.fromJson(json)
    val response = handler.handleMessage(wrapper.message)

    wrapper match {
      case Message(message, errorExchange, errorSubject) =>
        null
      case ErrorMessage(message, errorAction) =>
        null
    }

    response match {
      case Response.ACK =>
        channel.basicAck(deliveryTag, false)
      case Response.REJECT =>
        channel.basicNack(deliveryTag, false, false)
        sendErrorMessage(wrapper, HandlerResponse.fromJava(response))
      case Response.REQUEUE =>
        channel.basicNack(deliveryTag, false, true)
        sendErrorMessage(wrapper, HandlerResponse.fromJava(response))
    }
  }

  private def sendErrorMessage(wrapper: MessageWrapper, response: HandlerResponse.HandlerResponse): Unit = {
    wrapper match {
      case Message(message, Some(errorExchange), errorSubject) =>
          connection.publish(errorExchange, errorSubject.getOrElse(""), response, wrapper.message)
      case _ =>
    }
  }
}
