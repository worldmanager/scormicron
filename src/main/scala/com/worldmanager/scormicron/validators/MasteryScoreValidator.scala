package com.worldmanager.scormicron.validators

import scala.collection.mutable.ArrayBuffer

object MasteryScoreValidator {
    private val MinValue = 0
    private val MaxValue = 100
}

class MasteryScoreValidator extends Validator[Int] {

    import MasteryScoreValidator._

    override def validate(score: Int, errors: ArrayBuffer[Error]): Unit = {

        if (score < MinValue || score > MaxValue)
            errors += new Error("Invalid mastery score '%d' found; expected value between %d and %d".format(score, MinValue, MaxValue))
    }
}