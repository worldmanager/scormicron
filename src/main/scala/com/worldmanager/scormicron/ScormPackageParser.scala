package com.worldmanager.scormicron

import java.io.File
import java.nio.file.{Files, Path, Paths}

import com.worldmanager.scormicron.manifest.v112.{ItemType, ManifestType, MetadataType}
import com.worldmanager.scormicron.manifest.v121.LomType
import javax.xml.bind.JAXBElement
import net.lingala.zip4j.core.ZipFile
import org.w3c.dom.{Element, Node}

import scala.collection.JavaConverters._

object ScormPackageParser {
    private final val ManifestFileName      = "imsmanifest.xml"
    private final val ScoreTagName          = "adlcp:masteryscore"
    private final val MetadataschemeTagName = "metadatascheme"

    @throws(classOf[IllegalArgumentException])
    @throws(classOf[ParseException])
    def extractManifestFromScormZip(scormZip: File): File = {
        if (!scormZip.exists()) {
            throw new IllegalArgumentException("SCORM Zip file does not exist")
        }
        try {
            val zipFile = new ZipFile(scormZip)
            val tmpPath = Paths.get(System.getProperty("java.io.tmpdir"))
            val directory = Files.createTempDirectory(tmpPath, scormZip.getName + System.nanoTime.toString)
            directory.toFile.deleteOnExit()
            zipFile.extractAll(directory.toString)
            directory.resolve(ManifestFileName).toFile
        }
        catch {
            case throwable: Throwable => throw ParseException(throwable)
        }
    }

}

class ScormPackageParser(val manifest: File) {

    def this(path: Path) = this(path.resolve(ScormPackageParser.ManifestFileName).toFile)

    if (!manifest.exists) {
        throw ParseException("Manifest file '%s' not found".format(manifest))
    }

    if (manifest.getName != ScormPackageParser.ManifestFileName) {
        throw ParseException("Wrong manifest file name")
    }

    def parse: ScormPackage = {
        try {
            val manifestType = JaxbUtils.unmarshal[ManifestType](manifest)

            val organizationItem = manifestType.getOrganizations.getOrganization.asScala.headOption.flatMap(_.getItem.asScala.headOption)

            val entryPoint = parseEntryPoint(manifestType, organizationItem.map(_.getIdentifierref))
            val score = parseScore(organizationItem)

            val schema = parseSchema(manifestType: ManifestType)

            ScormPackage(manifestType, schema, entryPoint, score)
        }
        catch {
            case throwable: Throwable => throw ParseException(throwable)
        }

    }

    private def parseEntryPoint(manifestType: ManifestType, identifierRef: Option[String]): Option[String] = {
        identifierRef.flatMap(ref =>
            manifestType.getResources.getResource.asScala
                .find(_.getIdentifier == ref)
                .map(_.getHref)
        )
    }

    private def parseScore(organizationItem: Option[ItemType]): Option[String] = {
        organizationItem
            .flatMap(item => item.getAny.asScala.map(_.asInstanceOf[Element])
            .find(_.getTagName == ScormPackageParser.ScoreTagName)
            .map(_.getFirstChild.getNodeValue))
    }

    private def parseSchema(manifestType: ManifestType): Option[ScormSchema] = {
        Option(manifestType.getMetadata) match {
            case None => None
            case Some(metadata) =>
                val schema = Option(metadata.getSchema).flatMap(schema => Option(schema.trim))
                val schemaVersion = Option(metadata.getSchemaversion).flatMap(version => Option(version.trim))

                schemaVersion match {
                    case Some(version) => Some(ScormSchema(version, schema))
                    case None => parseMetadatascheme(metadata)
                }
        }
    }

    private def parseMetadatascheme(metadata: MetadataType): Option[ScormSchema] = {
        val contents = metadata.getAny.asScala.map(_.asInstanceOf[Node])
            .map(JaxbUtils.unmarshal[LomType])
            .flatMap(_.getMetametadata.getContent.asScala)


        val metadatascheme = contents
            .collect {case content if content.isInstanceOf[JAXBElement[_]] => content.asInstanceOf[JAXBElement[_]]}
            .filter(_.getName.getLocalPart.equalsIgnoreCase(ScormPackageParser.MetadataschemeTagName))
            .map(_.getValue.toString.trim)

        metadatascheme.headOption.map(ScormSchema(_))
    }

}
