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

  def unapply(name: String): Option[HandlerResponse] = {
    name.toUpperCase match {
      case "REJECT" => Some(REJECT)
      case "REQUEUE" => Some(REQUEUE)
      case "ACK" => Some(ACK)
      case _ => None
    }
  }

  def apply(name: String): HandlerResponse = {
    name match {
      case HandlerResponse(response) => response
      case _ => throw new IllegalArgumentException("Invalid Handler Response")
    }
  }

  def fromJava(response: MessageHandler.Response): HandlerResponse = {
    HandlerResponse(response.toString)
  }

  def toJava(response: HandlerResponse): MessageHandler.Response = {
    response match {
      case ACK => MessageHandler.Response.ACK
      case REJECT => MessageHandler.Response.REJECT
      case REQUEUE => MessageHandler.Response.REQUEUE
    }
  }
}


