package com.worldmanager.scormicron

import java.io.File
import java.nio.file.Path

object TestResources {

    val ManifestFileName = "imsmanifest.xml"

    private val resourcesPath = new File("src/test/resources/").toPath
    private val validPath   = resourcesPath.resolve("valid")
    private val invalidPath = resourcesPath.resolve("invalid")

    private val validDirectories: Seq[Path] = listSubDirectories(validPath.resolve("directory"))
    private val invalidDirectories: Seq[Path] = listSubDirectories(invalidPath.resolve("directory"))

    val validManifests: Seq[File] = {
        validDirectories.map(_.resolve("imsmanifest.xml").toFile)
    }

    val invalidManifests: Seq[File] = {
        invalidDirectories.map(_.resolve("imsmanifest.xml").toFile)
    }

    val validZips: Seq[File] = {
        listFiles(validPath.resolve("zip")).map(_.toFile)
    }

    val invalidZips: Seq[File] = {
        listFiles(invalidPath.resolve("zip")).map(_.toFile)
    }

    private def listSubDirectories(path: Path) = {
        path.toFile
            .list((current: File, name: String) => new File(current, name).isDirectory)
            .map(path.resolve).toSeq
    }

    private def listFiles(path: Path) = {
        path.toFile
            .list((current: File, name: String) => new File(current, name).isFile)
            .map(path.resolve).toSeq
    }

}