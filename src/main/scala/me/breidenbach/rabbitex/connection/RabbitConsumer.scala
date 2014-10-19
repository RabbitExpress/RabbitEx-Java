package me.breidenbach.rabbitex.connection

import java.io.IOException

import com.rabbitmq.client.{AMQP, Envelope, DefaultConsumer}
import me.breidenbach.rabbitex.MessageHandler.Response
import me.breidenbach.rabbitex.{MessageHandler, Consumer}

/**
 * Date: 10/12/14
 * Time: 9:35 PM
 * Copyright 2014 Kevin E. Breidenbach
 * @author Kevin E. Breidenbach
 */
private[connection] case class RabbitConsumer(connection: RabbitConnection, exchange: String, subject: String,
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

  private[connection] def defaultConsumer(): DefaultConsumer = {
    new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: Array[Byte]): Unit = {
        handleMessage(envelope, body)
      }
    }
  }

  private[connection] def handleMessage(envelope: Envelope, body: Array[Byte]) {
    val json = new String(body)
    val deliveryTag = envelope.getDeliveryTag
    val wrapper: MessageWrapper = MessageWrapper.fromJson(json)
    val response = handler.handleMessage(wrapper.message)

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
          connection.publishError(errorExchange, errorSubject.getOrElse(""), response, wrapper.message)
      case _ =>
    }
  }
}

