package com.worldmanager.scormicron

import java.io.File

import javax.xml.bind.JAXBContext
import javax.xml.transform.stream.StreamSource
import org.w3c.dom.Node

object JaxbUtils {

    def unmarshal[T](node: Node, clazz: Class[T]): T = {
        try {
            val jaxbContext = JAXBContext.newInstance(clazz)
            val unmarshaller = jaxbContext.createUnmarshaller
            unmarshaller.unmarshal(node, clazz).getValue
        }
        catch {
            case throwable: Throwable =>
                throw ParseException(throwable)
        }
    }

    def unmarshal[T](file: File, clazz: Class[T]): T = {
        try {
            val jaxbContext = JAXBContext.newInstance(clazz)
            val unmarshaller = jaxbContext.createUnmarshaller
            unmarshaller.unmarshal(new StreamSource(file), clazz).getValue
        }
        catch {
            case throwable: Throwable =>
                throw ParseException(throwable)
        }
    }

}
