# Scormicron

Scormicron is a library to parse `menifest.xml` into Java JAXB entity `ManifestType` based on [XML Schema Definition Files](https://scorm.com/scorm-explained/technical-scorm/content-packaging/xml-schema-definition-files/).
Also, it extracts the most important properties, such as schema, entry point and mastery score from `ManifestType` and, then, provide the validator for these properties.


Unfortunately, [SCORM official XSD](https://21w98o3yqgi738kmv7xrf9lj-wpengine.netdna-ssl.com/wp-content/assets/SchemaDefinitionFiles/zips/scorm12schemadefinition.zip) can't be used by JAXB directly. Few modifications have been done to allow JAXB to build `ManifestType`, which are

* In `ims_xml.xsd`, `xmlns="http://www.w3.org/XML/1998/namespace"` has been removed from `xsd:schema` tag.
* In `adlcp_rootv1p2.xsd`, it is extended `imscp_rootv1p1p2.xsd` by overriding `schema` and `schemeversion`. JAX doesn't support the overriding. The changes are the merge `imscp_rootv1p1p2.xsd` into `adlcp_rootv1p2.xsd`.
* In `imsmd_rootv1p2p1.xsd`, `namespace="##other"` has been replaced with `namespace="##any"` for the group type `<xsd:group name="grp.any">` as JAX doesn't work with `##other` namespace.   

The adjusted XSD have been set at `src/main/resources` folder. They are used to build `ManifestType` and all its elements by a gradle JAXB plugin [com.ewerk.gradle.plugins.jaxb2](https://plugins.gradle.org/plugin/com.ewerk.gradle.plugins.jaxb2). 

**Note**: These generated code are not maintained by the project but are built in the jar library.

TBC