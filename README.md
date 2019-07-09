# Scormicron

Scormicron is a library to validate a SCORM archive file. 

It contains 3 components

* `ManifestType` Entities
* `ScormPackageParser` Parser 
* `ScormPackageValidator` Validator

## `ManifestType` Entities

`ManifestType` entities are Java JAXB entities built based on [XML Schema Definition Files](https://scorm.com/scorm-explained/technical-scorm/content-packaging/xml-schema-definition-files/).

Unfortunately, [SCORM official XSD](https://21w98o3yqgi738kmv7xrf9lj-wpengine.netdna-ssl.com/wp-content/assets/SchemaDefinitionFiles/zips/scorm12schemadefinition.zip) can't be used by JAXB directly. A few modifications have been done to allow JAXB to build `ManifestType` entities. The modifications are:

* In `ims_xml.xsd`, `xmlns="http://www.w3.org/XML/1998/namespace"` has been removed from the `xsd:schema` tag.
* In `adlcp_rootv1p2.xsd`, it extends `imscp_rootv1p1p2.xsd` by overriding `schema` and `schemeversion`. JAX doesn't support the overriding. `imscp_rootv1p1p2.xsd` has been merged into `adlcp_rootv1p2.xsd` to work with JAXB.
* In `imsmd_rootv1p2p1.xsd`, `namespace="##other"` has been replaced with `namespace="##any"` for the group type `<xsd:group name="grp.any">` as JAX doesn't work with `##other` namespace.   

The altered XSDs are stored in the `src/main/resources` directory. They are used to build the `ManifestType` entities using a Gradle JAXB plugin [com.ewerk.gradle.plugins.jaxb2](https://plugins.gradle.org/plugin/com.ewerk.gradle.plugins.jaxb2). 

**Note**: These generated code is not maintained by the project but instead generated at compile time and is built into the JAR. 
  
## `ScormPackageParser` Parser

`ScormPackageParser` provides API to parse `imsmanifest.xml` into `ScormPackage`. 

`ScormPackage` contains the manifest type, scorm schema, entry point and master score for the SCORM.

```
case class ScormPackage
(
    manifestType: ManifestType,
    schema: Option[ScormSchema],
    entryPoint: Option[String],
    masteryScore: Option[String]
)
``` 

The parser converts `imsmanifest.xml` into a `ManifestType` first. Based on the `ManifestType`, it collects scorm schema from following paths: 

* `manifest` -> `metadata` -> `schema`
* `manifest` -> `metadata` -> `lom` -> `metametadata`
* `manifest` -> `organizations` -> `organization` -> `metadata` -> `schema`

Entry point is collected the from path: 

* `manifest` -> `resources` -> `resources` -> `identifier` -> `href`

Score is collected from path:

* `manifest` -> `organizations` -> `organization` -> `items` -> `item` -> `adlcp:masteryscore`


### Examples

Parse `imsmanifest.xml`
```
val scormPackage: ScormPackage = new ScormPackageParser(Paths.get("imsmanifest.xml"))
```

Parse `scorm.zip`
```
val zip = Paths.get("scorm.zip") 
val ScormPackageParser.extractManifestFromScormZip(zip.toFile)
val scormPackage: ScormPackage = new ScormPackageParser(ScormPackageParser.extractManifestFromScormZip(zip.toFile))
```
 
## `ScormPackageValidator` Validator
 
`ScormPackageValidator` validates the schema and score. 

* a valid schemas are `ADL SCORM 1.2` and `1.2`
* a valid score is an integer between `0` and `100`

### Examples

```
val validator = new ScormPackageValidator
val errors = ArrayBuffer.empty[Error]

validator.validate(scormPackage, errors)

val isValid = errors.isEmpty
```