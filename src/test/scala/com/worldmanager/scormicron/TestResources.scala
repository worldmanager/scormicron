package com.worldmanager.scormicron

import java.io.{File, FilenameFilter}
import java.nio.file.Path

object TestResources {

    val ManifestFileName = "imsmanifest.xml"

    private val BasePath       = "src/test/resources/"
    private val ValidDirPath   = BasePath + "valid/directory/"
    private val InvalidDirPath = BasePath + "invalid/directory/"
    private val ValidZipPath   = BasePath + "valid/zip/"
    private val InvalidZipPath = BasePath + "invalid/zip/"

    private val resourcesPath = new File("src/test/resources/").toPath
    val validPath = resourcesPath.resolve("valid")
    val invalidPath = resourcesPath.resolve("invalid")

    val validDirectories: Seq[Path] = listSubDirectories(validPath.resolve("directory"))
    val invalidDirectories: Seq[Path] = listSubDirectories(invalidPath.resolve("directory"))

    val validManifest: Seq[File] = {
        validDirectories.map(_.resolve("imsmanifest.xml").toFile)
    }

    val invalidManifest: Seq[File] = {
        invalidDirectories.map(_.resolve("imsmanifest.xml").toFile)
    }

    val validZips: Seq[File] = {
        listFiles(validPath.resolve("zip")).map(_.toFile)
    }

    val invalidZips: Seq[File] = {
        listFiles(invalidPath.resolve("zip")).map(_.toFile)
    }

    private def listSubDirectories(path: Path) = {
        path.toFile.list(new FilenameFilter() {
            def accept(current: File, name: String): Boolean = new File(current, name).isDirectory
        }).map(path.resolve).toSeq
    }

    private def listFiles(path: Path) = {
        path.toFile.list(new FilenameFilter() {
            def accept(current: File, name: String): Boolean = new File(current, name).isFile
        }).map(path.resolve).toSeq
    }

}