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

object MessageWrapper {
  def fromJson(json: String):MessageWrapper = {
    val parsedJson = Json.parse(json)
    val messageType = (parsedJson \ "messageType").toString()
    messageType match {
      case "\"MESSAGE\"" => Message.fromJson(parsedJson)
      case "\"ERROR\"" => ErrorMessage.fromJson(parsedJson)
    }
  }
}



case class ErrorMessage(message: String, errorAction: HandlerResponse.HandlerResponse) extends MessageWrapper {
  override val messageType = MessageType.ERROR

  private def convert(msg: ErrorMessage): (String, String, String) = {
    (msg.message, msg.messageType.toString, msg.errorAction.toString)
  }

  private implicit val errorMessageWrites = (
      (__ \ "message").write[String] ~
      (__ \ "messageType").write[String] ~
      (__ \ "errorAction").write[String]
    )(convert _)

  override def toJson: String = Json.toJson(this).toString()
}

object ErrorMessage {
  private implicit val errorMessageReads = (
      (__ \ "message").read[String] ~
      (__ \ "errorAction").read[String].map{ string => HandlerResponse.withName(string)}
    )((message, errorAction) => ErrorMessage(message, errorAction))

  private[connection] def fromJson(json: JsValue): ErrorMessage = Json.fromJson(json).
    getOrElse(throw new IllegalArgumentException("unable to parse json"))
}



case class Message(message: String, errorExchange: Option[String] = None, errorSubject: Option[String] = None) extends MessageWrapper {
  override val messageType = MessageType.MESSAGE

  private def convert(msg: Message): (String, String, Option[String], Option[String]) = {
    (msg.message, msg.messageType.toString, msg.errorExchange, msg.errorSubject)
  }

  private implicit val messageWrites = (
      (__ \ "message").write[String] ~
      (__ \ "messageType").write[String] ~
      (__ \ "errorExchange").writeNullable[String] ~
      (__ \ "errorSubject").writeNullable[String]
    )(convert _)

  override def toJson: String = Json.toJson(this).toString()
}

object Message {
  private implicit val messageReads = (
      (__ \ "message").read[String] ~
      (__ \ "errorExchange").readNullable[String] ~
      (__ \ "errorSubject").readNullable[String]
    )((message, errorExchange, errorSubject) => Message(message, errorExchange, errorSubject))

  private[connection] def fromJson(json: JsValue): Message = Json.fromJson(json).
    getOrElse(throw new IllegalArgumentException("unable to parse json"))
}