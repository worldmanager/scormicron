# Scormicron

Scormicron is a library to validate a SCORM archive file. 

It contains 3 components

* `ManifestType` Entities
* `ScormPackageParser` Parser 
* `ScormPackageValidator` Validator

## `ManifestType` Entities

`ManifestType` entities are Java JAXB entities built based on [XML Schema Definition Files](https://scorm.com/scorm-explained/technical-scorm/content-packaging/xml-schema-definition-files/).

Unfortunately, [SCORM official XSD](https://21w98o3yqgi738kmv7xrf9lj-wpengine.netdna-ssl.com/wp-content/assets/SchemaDefinitionFiles/zips/scorm12schemadefinition.zip) can't be used by JAXB directly. Few modifications have been done to allow JAXB to build `ManifestType`, which are

* In `ims_xml.xsd`, `xmlns="http://www.w3.org/XML/1998/namespace"` has been removed from `xsd:schema` tag.
* In `adlcp_rootv1p2.xsd`, it is extended `imscp_rootv1p1p2.xsd` by overriding `schema` and `schemeversion`. JAX doesn't support the overriding. The changes are the merge `imscp_rootv1p1p2.xsd` into `adlcp_rootv1p2.xsd`.
* In `imsmd_rootv1p2p1.xsd`, `namespace="##other"` has been replaced with `namespace="##any"` for the group type `<xsd:group name="grp.any">` as JAX doesn't work with `##other` namespace.   

The adjusted XSD have been set at `src/main/resources` folder. They are used to build `ManifestType` and all its elements by a gradle JAXB plugin [com.ewerk.gradle.plugins.jaxb2](https://plugins.gradle.org/plugin/com.ewerk.gradle.plugins.jaxb2). 

**Note**: These generated code are not maintained by the project but are built in the jar library.
  
## `ScormPackageParser` Parser

`ScormPackageParser` provides API to parse `imsmanifest.xml` into `ScormPackage`. 

`ScormPackage` contains manifest type, scorm schema, entry point and master score.

```
case class ScormPackage
(
    manifestType: ManifestType,
    schema: Option[ScormSchema],
    entryPoint: Option[String],
    masteryScore: Option[String]
)
``` 

The parse convert `imsmanifest.xml` to `ManifestType` first. Based on `ManifestType`, it collects scorm schema from following paths. 

* `manifest` -> `metadata` -> `schema`
* `manifest` -> `metadata` -> `lom` -> `metametadata`
* `manifest` -> `organizations` -> `organization` -> `metadata` -> `schema`

Entry point is collected from path 

* `manifest` -> `resources` -> `resources` -> `identifier` -> `href`

Score is collected from path 

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
 
`ScormPackageValidator` validate schema and score. 

* valid schema are `ADL SCORM 1.2` and `1.2`
* valid score is integer between `0` and `100`

### Examples

```
val validator = new ScormPackageValidator
val errors = ArrayBuffer.empty[Error]

validator.validate(scormPackage, errors)

val isValid = errors.isEmpty
```