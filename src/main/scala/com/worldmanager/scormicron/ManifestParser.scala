package com.worldmanager.scormicron

import java.io.File
import java.nio.file.{Files, Paths}

import com.worldmanager.scormicron.manifest.v112.ManifestType
import net.lingala.zip4j.core.ZipFile

object ManifestParser {
    private final val ManifestFileName = "imsmanifest.xml"

    @throws(classOf[IllegalArgumentException])
    @throws(classOf[ParseException])
    def extractManifestFromScormZip(scormZip: File): File = {
        if (!scormZip.exists()) {
            throw new IllegalArgumentException("SCORM Zip file does not exist")
        }
        try {
            val zipFile = new ZipFile(scormZip)
            val directory = Files.createTempDirectory(Paths.get(scormZip.getParent), scormZip.getName + System.nanoTime.toString)
            directory.toFile.deleteOnExit()
            zipFile.extractAll(directory.toString)
            directory.resolve(ManifestFileName).toFile
        }
        catch {
            case throwable: Throwable =>
                throw ParseException(throwable)
        }
    }

}

class ManifestParser(val manifest: File) {

    if (!manifest.exists) {
        throw new ParseException("Manifest file '%s' not found".format(manifest))
    }

    if (manifest.getName != ManifestParser.ManifestFileName) {
        throw new ParseException("Wrong manifest file name")
    }

    def parse: ManifestType = JaxbUtils.unmarshal(manifest, classOf[ManifestType])

}
