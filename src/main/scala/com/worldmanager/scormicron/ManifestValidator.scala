package com.worldmanager.scormicron

import com.worldmanager.scormicron.manifest.v121.ManifestType
import com.worldmanager.scormicron.validators.Validator

import scala.collection.mutable.ArrayBuffer

object ManifestValidator {
    private val ValidSchemaValue         = "ADL SCORM"
    private val ValidSchemaVersionValue  = "1.2"
    private val ValidSchemeValue         = "ADL SCORM 1.2"

}

class ManifestValidator extends Validator[ManifestType]{

    override def validate(manifestType: ManifestType, errors: ArrayBuffer[Error]): Unit = {

    }
}
