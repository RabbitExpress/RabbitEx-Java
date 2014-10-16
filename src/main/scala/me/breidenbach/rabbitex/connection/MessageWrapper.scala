package me.breidenbach.rabbitex.connection

import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Writes._

/**
 * Date: 10/12/14
 * Time: 8:41 PM
 * Copyright 2014 Kevin E. Breidenbach
 * @author Kevin E. Breidenbach
 */
trait MessageWrapper {
  def message: String
  def messageType: MessageType.MessageType
  def toJson: String
}

case class ErrorMessage(message: String, errorAction: HandlerResponse.HandlerResponse) extends MessageWrapper {
  override val messageType = MessageType.ERROR

  def convert(msg: ErrorMessage): (String, String, String) = {
    (msg.message, msg.messageType.toString, msg.errorAction.toString)
  }

  implicit val errorMessageWrites = (
      (__ \ "message").write[String] ~
      (__ \ "messageType").write[String] ~
      (__ \ "errorAction").write[String]
    )(convert _)

  override def toJson: String = Json.toJson(this).toString()
}

case class Message(message: String, errorExchange: String = null, errorSubject: String = null) extends MessageWrapper {
  override val messageType = MessageType.MESSAGE

  def convert(msg: Message): (String, String, Option[String], Option[String]) = {
    val errorEx = if (msg.errorExchange == null) None else Some(msg.errorExchange)
    val errorSub = if (msg.errorSubject == null) None else Some(msg.errorSubject)
    (msg.message, msg.messageType.toString, errorEx, errorSub)
  }

  implicit val messageWrites = (
      (__ \ "message").write[String] ~
      (__ \ "messageType").write[String] ~
      (__ \ "errorExchange").writeNullable[String] ~
      (__ \ "errorSubject").writeNullable[String]
    )(convert _)
  override def toJson: String = Json.toJson(this).toString()
}

private object ErrorMessageJSONHandler {
  implicit val errorMessageReads = (
      (__ \ "message").read[String] ~
      (__ \ "errorAction").read[String].map{ string => HandlerResponse.withName(string)}
    )(ErrorMessage)

  def fromJson(json: JsValue): ErrorMessage = Json.fromJson(json).
    getOrElse(throw new IllegalArgumentException("unable to parse json"))
}

private object MessageJSONHandler {
  def convert(msg:(String, Option[String], Option[String])): Message = {
    msg match {
      case (message, None, _) => Message(message)
      case (message, Some(errorExchange), None) => Message(message, errorExchange)
      case (message, Some(errorExchange), Some(errorSubject)) => Message(message, errorExchange, errorSubject)
    }
  }

  implicit val messageReads = (
      (__ \ "message").read[String] ~
      (__ \ "errorExchange").readNullable[String] ~
      (__ \ "errorSubject").readNullable[String]
    ).tupled

  def fromJson(json: JsValue): Message = {
      convert(Json.fromJson(json).getOrElse(throw new IllegalArgumentException("unable to parse json")))
  }
}

object MessageWrapper {
  def fromJson(json: String):MessageWrapper = {
    val parsedJson = Json.parse(json)
    val messageType = (parsedJson \ "messageType").toString()
    messageType match {
      case "\"MESSAGE\"" => MessageJSONHandler.fromJson(parsedJson)
      case "\"ERROR\"" => ErrorMessageJSONHandler.fromJson(parsedJson)
    }
  }
}

