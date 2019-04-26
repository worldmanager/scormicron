package com.worldmanager.scormicron

import scala.collection.mutable.ArrayBuffer
import scala.util.Try

class ScormPackageValidatorSpec extends BaseSpec {

    import TestResources._

    private val manifestWithInvalidData = invalidManifest
        .map(file => (file, Try(new ScormPackageParser(file).parse).toOption))
            .filter(p => p._2.isDefined && !p._1.toString.contains("invalid-empty-href"))
            .map(p => (p._1, p._2.get))

    describe(classOf[ScormPackageValidator].getCanonicalName) {
        describe("validate()") {
            validManifest.foreach { file =>
                describe("Given valid manifest %s".format(file)) {
                    it("should returns empty errors") {
                        val scormPackage = new ScormPackageParser(file).parse

                        Given("file: %s".format(file))
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
            manifestWithInvalidData.foreach{ p =>
                describe("Given manifest with invalid data %s".format(p._1)) {
                    it("should returns errors") {
                        val scormPackage = p._2
                        val validator = new ScormPackageValidator
                        val errors = ArrayBuffer.empty[Error]

                        validator.validate(scormPackage, errors)

                        Given("file: %s".format(p._1))
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
