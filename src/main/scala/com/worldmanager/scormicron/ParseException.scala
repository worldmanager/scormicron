package com.worldmanager.scormicron

object ParseException {
    def apply(cause: Throwable): ParseException = {
        require(cause != null)
        ParseException(cause.getMessage, Some(cause))
    }

    def apply(message: String): ParseException = ParseException(message, None)
}

case class ParseException(message: String, cause: Option[Throwable]) extends RuntimeException(message, cause.orNull)
