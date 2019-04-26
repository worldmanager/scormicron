package com.worldmanager.scormicron

import scala.collection.mutable.ArrayBuffer
import scala.util.Try

object ScormPackageValidator {

    private val ScoreMinValue = 0
    private val ScoreMaxValue = 100
}

class ScormPackageValidator extends Validator[ScormPackage] {
    import ScormPackageValidator._

    override def validate(scormPackage: ScormPackage, errors: ArrayBuffer[Error]): Unit = {

        errors ++= validateSchema(scormPackage.schema)
        errors ++= validateMasteryScore(scormPackage.masteryScore)

    }

    private def validateSchema(scormSchema: Option[ScormSchema]): ArrayBuffer[Error] = {
        val errors = ArrayBuffer.empty[Error]

        scormSchema match {
            case None => errors += new Error("No Metadata")
            case Some(schema) =>
                val defaultSchema = ScormSchema.Default

                val isValidVersion = defaultSchema.toString == schema.toString ||           // schema == ADL SCORM 1.2
                    (schema.schema.isEmpty && schema.version == defaultSchema.version)      // schema == 1.2


                if (!isValidVersion) {
                    errors += new Error("Invalid Schema: %s is not equal to %s".format(schema, defaultSchema))
                }
        }
        errors
    }

    private def validateMasteryScore(masteryScore: Option[String]): ArrayBuffer[Error]= {
        val errors = ArrayBuffer.empty[Error]

        masteryScore.foreach { scoreString =>
            Try(scoreString.toInt).toOption match {
                case None => errors += new Error("Invalid mastery score format. %s is not integer".format(scoreString))
                case Some(score) =>
                    if (score < ScoreMinValue || score > ScoreMaxValue) {
                        errors += new Error("Invalid mastery score '%d' found; expected value between %d and %d".format(score, ScoreMinValue, ScoreMaxValue))
                    }
            }
        }

        errors
    }

}
