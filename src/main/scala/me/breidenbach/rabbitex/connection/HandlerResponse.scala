package me.breidenbach.rabbitex.connection

import me.breidenbach.rabbitex.MessageHandler

/**
 * Date: 10/12/14
 * Time: 11:14 PM
 * Copyright 2014 Kevin E. Breidenbach
 * @author Kevin E. Breidenbach
 */

object HandlerResponse extends Enumeration {
  type HandlerResponse = Value
  val REJECT, REQUEUE, ACK = Value

  def fromJava(response: MessageHandler.Response): HandlerResponse = {
    HandlerResponse.withName(response.toString)
  }

  def toJava(response: HandlerResponse): MessageHandler.Response = {
    response match {
      case ACK => MessageHandler.Response.ACK
      case REJECT => MessageHandler.Response.REJECT
      case REQUEUE => MessageHandler.Response.REQUEUE
    }
  }
}


