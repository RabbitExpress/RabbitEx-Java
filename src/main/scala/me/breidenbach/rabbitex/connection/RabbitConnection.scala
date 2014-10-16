package me.breidenbach.rabbitex.connection

import java.io.IOException
import java.util
import java.util.Calendar

import com.rabbitmq.client.{AMQP, Connection, ConnectionFactory}
import me.breidenbach.rabbitex.{Consumer, MessageHandler, Options, RabbitEx}
import me.breidenbach.rabbitex.Options._
import scala.collection.mutable

/**
 * Date: 10/12/14
 * Time: 7:14 PM
 * Copyright 2014 Kevin E. Breidenbach
 * @author Kevin E. Breidenbach
 */
private[connection] case class RabbitConnection(host: String, port: Int, virtualHost: String, username: String,
                                                password: String, factory: ConnectionFactory) extends RabbitEx {

  def this(host: String, port: Int, virtualHost: String = "", username: String = "", password: String = "") =
    this(host, port, virtualHost, username, password, null)

  val key: String = host + port + virtualHost + username
  val connection = prepareConnection()
  var closed = false

  private val EXCHANGE_TYPE = "topic"

  override def publish(exchange: String, subject: String, message: String, options: util.Map[Options, String]): Unit = {
    val errorExchange = if (options.containsKey(ERROR_EXCHANGE)) options.get(ERROR_EXCHANGE) else ""
    val errorSubject = if (options.containsKey(ERROR_SUBJECT)) options.get(ERROR_SUBJECT) else ""
    val wrapper: MessageWrapper = Message(message = message, errorExchange = errorExchange, errorSubject = errorSubject)
    publish(exchange, subject, wrapper)
  }

  def publish(exchange: String, subject: String, errorAction: HandlerResponse.HandlerResponse, message: String): Unit = {
    val wrapper: MessageWrapper = ErrorMessage(message, errorAction)
    publish(exchange, subject, wrapper)
  }

  def publish(exchange: String, subject: String, wrapper: MessageWrapper): Unit = {
    val builder =  new AMQP.BasicProperties.Builder()
    val json = wrapper.toJson
    val headers = new util.HashMap[String, AnyRef]()
    headers.put("type", wrapper.messageType)
    builder.deliveryMode(2)
    builder.timestamp(Calendar.getInstance().getTime)
    builder.headers(headers)
    try {
      val channel = connection.createChannel()
      channel.exchangeDeclare(exchange, EXCHANGE_TYPE, true)
      channel.basicPublish(exchange, subject, builder.build(), json.getBytes)
    } catch {
      case e: IOException =>throw new RabbitConnectionException("Unable to publish message", e);
    }
  }

  override def consumer(exchange: String, subject: String, queue: String, handler: MessageHandler): Consumer = {
    new RabbitConsumer(this, exchange, subject, queue, handler)
  }

  override def close(): Unit = {
    RabbitConnection.remove(this)
    closed = true
  }

  private def prepareConnection(): Connection = {
    try {
      factory.newConnection()
    }
    catch {
      case e: IOException =>
        throw new RabbitConnectionException("Unable to create connection: " + e.getMessage, e)
    }
  }
}

object RabbitConnection {
  private val connections = new mutable.WeakHashMap[String, RabbitConnection]

  def rabbitConnection(host: String, port: Int, virtualHost: String = "", username: String = "", password: String = ""): RabbitEx = {
    val conn = new RabbitConnection(host, port, virtualHost, username, password)
    connections.contains(conn.key) match {
      case true =>
        val connection = connections(conn.key)
        if (connection.closed) cacheNewConnection(conn) else connection
      case _ =>
        cacheNewConnection(conn)
    }
  }

  private[connection] def remove(rabbitConnection: RabbitConnection): Unit = {
    connections.remove(rabbitConnection.key)
  }

  private def cacheNewConnection(rabbitConnection: RabbitConnection): RabbitEx = {
    val factory = new ConnectionFactory
    factory.setHost(rabbitConnection.host)
    factory.setPort(rabbitConnection.port)
    if (!rabbitConnection.username.isEmpty) factory.setUsername(rabbitConnection.username)
    if (!rabbitConnection.password.isEmpty) factory.setPassword(rabbitConnection.password)

    val rabbitExConnection = rabbitConnection.copy(factory = factory)
    connections(rabbitExConnection.key) = rabbitExConnection
    rabbitExConnection
  }
}


