package com.worldmanager.scormicron

import scala.collection.mutable.ArrayBuffer
import scala.util.Try

class ManifestValidatorSpec extends BaseSpec {

    import TestResources._

    private val manifestWithInvalidData = invalidManifest
        .map(file => (file, Try(new ManifestParser(file).parse).toOption))
            .filter(p => p._2.isDefined && !p._1.toString.contains("invalid-empty-href"))

    describe(classOf[ManifestValidator].getCanonicalName) {
        describe("validate()") {
            validManifest.foreach { file =>
                describe("Given valid manifest %s".format(file)) {
                    it("should returns empty errors") {
                        val manifest = new ManifestParser(file).parse

                        val validator = new ManifestValidator
                        val errors = ArrayBuffer.empty[Error]

                        validator.validate(manifest, errors)

                        errors.isEmpty shouldBe true
                    }
                }
            }
            manifestWithInvalidData.foreach { p =>
                describe("Given valid manifest %s".format(p._1)) {
                    it("should returns errors") {
                        val validator = new ManifestValidator
                        val errors = ArrayBuffer.empty[Error]

                        validator.validate(p._2.get, errors)

                        errors.foreach(error => info(error.getMessage))
                        errors.isEmpty shouldBe false
                    }
                }
            }
        }
    }

}
