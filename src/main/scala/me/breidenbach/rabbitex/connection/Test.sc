import me.breidenbach.rabbitex.connection._

val msg = Message("Noddy")
val errorMsg = ErrorMessage("Problem", HandlerResponse.REQUEUE)

val json = msg.toJson
val errorJson = errorMsg.toJson

val newMsg = MessageWrapper.fromJson(json)
val newErrorMsh = MessageWrapper.fromJson(errorJson)
val msgWithError = Message(errorJson)
val msgWithErrorJson = msgWithError.toJson
val receivedMsgWithError = MessageWrapper.fromJson(msgWithErrorJson)
 receivedMsgWithError.message

