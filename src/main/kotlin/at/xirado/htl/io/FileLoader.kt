package at.xirado.htl.io

import net.dv8tion.jda.api.utils.data.DataObject
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths

class FileLoader {
    companion object {

        fun loadResourceAsJson(fileName: String): DataObject {
            val inputStream = FileLoader::class.java.getResourceAsStream("/$fileName")?: throw FileNotFoundException("Resource $fileName not found")
            return DataObject.fromJson(inputStream)
        }

        fun loadResourceAsYaml(fileName: String): DataObject {
            val inputStream = FileLoader::class.java.getResourceAsStream("/$fileName")?: throw FileNotFoundException("Resource $fileName not found")
            return DataObject.fromYaml(inputStream)
        }

        fun loadFileAsJson(fileName: String, copyFromResources: Boolean): DataObject {
            val file = File(fileName)
            check(fileName, file, copyFromResources)
            return DataObject.fromJson(FileInputStream(file))
        }

        fun loadFileAsYaml(fileName: String, copyFromResources: Boolean): DataObject {
            val file = File(fileName)
            check(fileName, file, copyFromResources)
            return DataObject.fromYaml(FileInputStream(file))
        }

        private fun check(fileName: String, file: File, copyFromResources: Boolean) {
            if (!file.exists()) {
                if (!copyFromResources) {
                    throw FileNotFoundException("File $fileName was not found!")
                }

                val inputStream = FileLoader::class.java.getResourceAsStream("/$fileName")
                    ?: throw FileNotFoundException("File $fileName was not found in resources folder!")

                val path = Paths.get("${getJarPath()}/$fileName")

                Files.copy(inputStream, path)
            }
        }

        private fun getJarPath(): String {
            val codeSource = FileLoader::class.java.protectionDomain.codeSource
            val jarFile = File(codeSource.location.toURI().path)
            return jarFile.parentFile.path
        }
    }
}

fun DataObject.anyNull(vararg keys: String): Boolean {
    for (key in keys) {
        if (isNull(key)) {
            return true
        }
    }
    return false
}

fun DataObject.nullOrBlank(key: String): Boolean {
    return isNull(key) || getString(key).isBlank()
}