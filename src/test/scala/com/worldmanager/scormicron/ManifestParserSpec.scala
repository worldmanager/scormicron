package com.worldmanager.scormicron

import com.worldmanager.scormicron.manifest.v112.ManifestType

class ManifestParserSpec extends BaseSpec {

    import TestResources._

    describe(classOf[ManifestParser].getCanonicalName) {
        describe("parse") {
            describe("Given valid manifest") {
                validManifest.foreach { manifest =>
                    it("should parse valid %s to %s".format(manifest, classOf[ManifestType].getName)) {
                        try {
                            val factory = new ManifestParser(manifest)
                            val manifestType = factory.parse

                            manifestType shouldNot be(null)
                        }
                        catch {
                            case ex: ParseException =>
                                info(ex.getMessage)
                                throw ex
                        }
                    }
                }
            }
        }

        describe("extractManifestFromScormZip") {
            describe("Given valid SCORM Zip") {
                validZips.foreach { zip =>
                    it("should extract %s from %s".format(ManifestFileName, zip.getName)) {
                        try {
                            val manifestFile = ManifestParser.extractManifestFromScormZip(zip)

                            manifestFile shouldNot be(null)
                        }
                        catch {
                            case ex: ParseException =>
                                info(ex.getMessage)
                                throw ex
                        }
                    }
                }
            }
        }
    }

}
