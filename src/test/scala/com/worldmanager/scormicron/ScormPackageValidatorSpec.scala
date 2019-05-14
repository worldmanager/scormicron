package com.worldmanager.scormicron

import scala.collection.mutable.ArrayBuffer
import scala.util.Try

class ScormPackageValidatorSpec extends BaseSpec {

    import TestResources._

    private val manifestsWithInvalidData = invalidManifests
        .map(file => (file, Try(new ScormPackageParser(file).parse).toOption))
            .filter{ case (manifest, scormPackage) => scormPackage.isDefined && !manifest.toString.contains("invalid-empty-href")}
            .map(p => (p._1, p._2.get))

    describe(classOf[ScormPackageValidator].getCanonicalName) {
        describe("validate()") {
            validManifests.foreach { manifest =>
                describe("Given valid manifest %s".format(manifest)) {
                    it("should returns empty errors") {
                        val scormPackage = new ScormPackageParser(manifest).parse

                        Given("manifest: %s".format(manifest))
                        Given("schema: %s".format(scormPackage.schema))
                        Given("score: %s".format(scormPackage.masteryScore))
                        Given("entryPoint: %s".format(scormPackage.entryPoint))


                        val validator = new ScormPackageValidator
                        val errors = ArrayBuffer.empty[Error]

                        validator.validate(scormPackage, errors)

                        errors.foreach(error => info(error.getMessage))

                        errors.isEmpty shouldBe true
                    }
                }
            }
            manifestsWithInvalidData.foreach { manifest =>
                describe("Given manifest with invalid data %s".format(manifest._1)) {
                    it("should returns errors") {
                        val scormPackage = manifest._2
                        val validator = new ScormPackageValidator
                        val errors = ArrayBuffer.empty[Error]

                        validator.validate(scormPackage, errors)

                        Given("manifest: %s".format(manifest._1))
                        Given("score: %s".format(scormPackage.masteryScore))
                        Given("entryPoint: %s".format(scormPackage.entryPoint))

                        errors.foreach(error => info(error.getMessage))
                        errors.isEmpty shouldBe false
                    }
                }
            }
        }
    }

}
