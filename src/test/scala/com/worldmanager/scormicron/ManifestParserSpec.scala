package com.worldmanager.scormicron

class ManifestParserSpec extends BaseSpec {

    import TestResources._

    describe(classOf[ScormPackageParser].getCanonicalName) {
        describe("parse") {
            describe("Given valid manifest") {
                validManifests.foreach { manifest =>
                    it("should parse valid %s to %s".format(manifest, classOf[ScormPackageParser].getName)) {
                        try {
                            val factory = new ScormPackageParser(manifest)
                            val manifestType = factory.parse

                            manifestType should not be null
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
                            val manifestFile = ScormPackageParser.extractManifestFromScormZip(zip)

                            manifestFile should not be null
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
