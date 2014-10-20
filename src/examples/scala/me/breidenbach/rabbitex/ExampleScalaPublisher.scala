package me.breidenbach.rabbitex

import me.breidenbach.rabbitex.connection.RabbitFactory

/**
 * Date: 10/19/14
 * Time: 10:01 PM
 * Copyright 2014 Kevin E. Breidenbach
 * @author Kevin E. Breidenbach
 */
case class ExampleScalaPublisher(host: String, port: Int, exchange: String, subject: String) {
  val rabbitEx = RabbitFactory.newConnection(host, port)

  def sendMessage(message: String): Unit = {
    rabbitEx.publish(exchange, subject, message, null)
    rabbitEx.close()
  }
}

object ExampleScalaPublisher {
  val HOSTNAME = "127.0.0.1"
  val PORT = 5672
  val EXCHANGE = "my-exchange"
  val SUBJECT = "my-subject"
  val MESSAGE = "Hello World!"

  def main(args: Array[String]): Unit = {
    ExampleScalaPublisher(HOSTNAME, PORT, EXCHANGE, SUBJECT).sendMessage(MESSAGE)
    sys.exit()
  }
}