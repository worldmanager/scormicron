package com.worldmanager.scormicron

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl
import com.worldmanager.scormicron.manifest.v112.{ItemType, ManifestType, MetadataType, OrganizationType}
import com.worldmanager.scormicron.manifest.v121.{LomType, ObjectFactory}
import javax.xml.bind.JAXBElement

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import scala.util.{Failure, Success, Try}

object ManifestValidator {
    private val ValidSchemaValue         = "ADL SCORM"
    private val ValidSchemaVersionValue  = "1.2"
    private val ValidSchemeValue         = "ADL SCORM 1.2"

    private val ScoreTagName  = "adlcp:masteryscore"
    private val ScoreMinValue = 0
    private val ScoreMaxValue = 100
}

class ManifestValidator extends Validator[ManifestType] {
    import ManifestValidator._

    override def validate(manifestType: ManifestType, errors: ArrayBuffer[Error]): Unit = {

        errors ++= validateMetadata(manifestType)
        errors ++= validateMasteryScore(manifestType)

    }

    private def validateMetadata(manifest: ManifestType): ArrayBuffer[Error] = {
        val errors = ArrayBuffer.empty[Error]
        Option(manifest.getMetadata) match {
            case None => errors += new Error("No Metadata")
            case Some(metadata) =>
                val schema = Option(metadata.getSchema)
                val schemaVersion = Option(metadata.getSchemaversion)

                if (schemaVersion.isDefined) {
                    schema.foreach { schema =>
                        if (schema != ValidSchemaValue) errors += new Error("Invalid Schema: %s".format(schema))
                    }
                    schemaVersion.foreach { schemaVersion =>
                        if (schemaVersion != ValidSchemaVersionValue) errors += new Error("Invalid Schema Version: %s".format(ValidSchemaVersionValue))
                    }
                }
                else {
                    val metadatascheme = getMetadatascheme(metadata)
                    if (metadatascheme.isEmpty) {
                        errors += new Error("No Metadata")
                    }
                    else {
                        metadatascheme.filterNot(_ == ValidSchemeValue).foreach { scheme =>
                            errors += new Error("Invalid Metadatascheme: %s".format(scheme))
                        }
                    }
                }
        }
        errors
    }

    private def getMetadatascheme(metadata: MetadataType): Set[String] = {
        val objs = Option(metadata.getAny).map(_.asScala.toIterable).getOrElse(Iterable.empty)

        objs.flatMap(obj => Try(JaxbUtils.unmarshal(obj.asInstanceOf[ElementNSImpl], classOf[LomType])).toOption)
            .flatMap(lomType => Option(lomType.getMetametadata))
            .flatMap(metametadata => Option(metametadata.getContent))
            .flatMap(_.asScala)
            .filter(_.isInstanceOf[JAXBElement[_]])
            .map(_.asInstanceOf[JAXBElement[_]])
            .filter(el => el.getName.getLocalPart.equalsIgnoreCase("metadatascheme"))
            .map(_.getValue.toString.trim)
            .toSet

    }

    private def validateMasteryScore(manifest: ManifestType): ArrayBuffer[Error]= {
        val errors = ArrayBuffer.empty[Error]

        Try(getMasteryScore(manifest)) match {
            case Failure(error) =>  errors += new Error(error)
            case Success(scores) => scores.foreach { scoreString =>
                Try(scoreString.toInt).toOption match {
                    case None => errors += new Error("Invalid mastery score format. %s is not integer".format(scoreString))
                    case Some(score) =>
                        if (score < ScoreMinValue || score > ScoreMaxValue) {
                            errors += new Error("Invalid mastery score '%d' found; expected value between %d and %d".format(score, ScoreMinValue, ScoreMaxValue))
                        }
                }
            }
        }

        errors
    }

    private def getMasteryScore(manifest: ManifestType): Set[String] = {
        manifest.getOrganizations.getOrganization.asScala
            .flatMap(organization => Option(organization.getItem))
            .flatMap(_.asScala)
            .flatMap(objs => Option(objs.getAny))
            .flatMap(_.asScala)
            .filter(_.isInstanceOf[ElementNSImpl])
            .map(_.asInstanceOf[ElementNSImpl])
            .filter(_.getTagName.equalsIgnoreCase(ScoreTagName))
            .flatMap(content => Option(content.getTextContent))
            .map(_.trim)
            .toSet
    }
}
