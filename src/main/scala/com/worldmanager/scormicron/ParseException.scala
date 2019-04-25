package com.worldmanager.scormicron

object ParseException {
    def apply(cause: Throwable): ParseException = ParseException(cause.getMessage, cause)
}

case class ParseException(message: String, cause: Throwable) extends RuntimeException(message, cause) {
    def this(message: String) = this(message, null)
}