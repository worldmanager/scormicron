package com.worldmanager.scormicron

import java.io.File

import javax.xml.bind.JAXBContext
import javax.xml.transform.stream.StreamSource
import org.w3c.dom.Node

import scala.reflect.ClassTag

object JaxbUtils {

    def unmarshal[T: ClassTag](node: Node): T = {
        try {
            val supportedTag = implicitly[ClassTag[T]].runtimeClass
            val jaxbContext = JAXBContext.newInstance(supportedTag)
            val unmarshaller = jaxbContext.createUnmarshaller
            unmarshaller.unmarshal(node, supportedTag).getValue.asInstanceOf[T]
        }
        catch {
            case throwable: Throwable => throw ParseException(throwable)
        }
    }

    def unmarshal[T: ClassTag](file: File): T = {
        try {
            val supportedTag = implicitly[ClassTag[T]].runtimeClass
            val jaxbContext = JAXBContext.newInstance(supportedTag)
            val unmarshaller = jaxbContext.createUnmarshaller
            unmarshaller.unmarshal(new StreamSource(file), supportedTag).getValue.asInstanceOf[T]
        }
        catch {
            case throwable: Throwable => throw ParseException(throwable)
        }
    }

}
