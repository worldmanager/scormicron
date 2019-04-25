package com.worldmanager.scormicron.validators

import scala.collection.mutable.ArrayBuffer

trait Validator[T] {

    def validate(t: T, errors: ArrayBuffer[Error]): Unit

    def validate(t: T): Unit = validate(t, ArrayBuffer.empty[Error])

}
