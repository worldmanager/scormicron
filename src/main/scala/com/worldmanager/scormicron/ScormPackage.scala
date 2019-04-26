package com.worldmanager.scormicron

import com.worldmanager.scormicron.manifest.v112.ManifestType

case class ScormPackage
(
    manifestType: ManifestType,
    schema: Option[ScormSchema],
    entryPoint: Option[String],
    masteryScore: Option[String]
)

object ScormSchema {

    val Default: ScormSchema = ScormSchema("1.2", Some("ADL SCORM"))

    def apply(version: String): ScormSchema = ScormSchema(version, None)

}

case class ScormSchema(version: String, schema: Option[String]) {

    override def toString: String = {
        schema match {
            case None => version
            case Some(schema) => Seq(schema, version).mkString(" ")
        }
    }

}

