package me.breidenbach.rabbitex

import me.breidenbach.rabbitex.MessageHandler.Response
import me.breidenbach.rabbitex.connection.RabbitFactory

/**
 * Date: 10/19/14
 * Time: 10:01 PM
 * Copyright 2014 Kevin E. Breidenbach
 * @author Kevin E. Breidenbach
 */
case class ExampleScalaConsumer(host: String, port: Int, exchange: String, subject: String, queue: String) extends MessageHandler {
  val rabbitEx = RabbitFactory.newConnection(host, port)
  val consumer = rabbitEx.consumer(exchange, subject, queue, this)
  var messageNumber = 1

  def start() = consumer.start()

  override def handleMessage(message: String): Response = {
    println(f"Message Number: $messageNumber: $message%s")
    messageNumber += 1
    Response.ACK
  }
}

object ExampleScalaConsumer {
  val HOSTNAME = "127.0.0.1"
  val PORT = 5672
  val EXCHANGE = "my-exchange"
  val SUBJECT = "my-subject"
  val QUEUE = "my-scala-queue"

  def main(args: Array[String]): Unit = {
    val consumer = ExampleScalaConsumer(HOSTNAME, PORT, EXCHANGE, SUBJECT, QUEUE)
    consumer.start()
  }
}
