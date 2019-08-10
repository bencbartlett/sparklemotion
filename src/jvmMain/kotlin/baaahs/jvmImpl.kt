package baaahs

import baaahs.io.Fs
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.*
import kotlin.streams.toList

actual fun doRunBlocking(block: suspend () -> Unit) = runBlocking { block() }

actual fun getResource(name: String): String {
    return Pinky::class.java.classLoader.getResource(name).readText()
}

actual fun getTimeMillis(): Long = System.currentTimeMillis()

actual fun decodeBase64(s: String): ByteArray = Base64.getDecoder().decode(s)

@UseExperimental(ExperimentalStdlibApi::class)
class RealFs(private val basePath: Path) : Fs {

    override fun listFiles(path: String): List<String> {
        return Files.list(resolve(path)).map { it.fileName.toString() }.toList()
    }

    override fun loadFile(path: String): String? {
        val destPath = resolve(path)
        try {
            return Files.readAllBytes(destPath).decodeToString()
        } catch (e: java.nio.file.NoSuchFileException) {
            return null
        }
    }

    override fun createFile(path: String, content: ByteArray, allowOverwrite: Boolean) {
        val destPath = resolve(path)
        Files.createDirectories(destPath.parent)
        Files.write(destPath, content, if (allowOverwrite) StandardOpenOption.CREATE else StandardOpenOption.CREATE_NEW)
    }

    override fun createFile(path: String, content: String, allowOverwrite: Boolean) {
        createFile(path, content.encodeToByteArray(), allowOverwrite)
    }

    private fun resolve(path: String): Path {
        val safeName = path.replace(Regex("^(\\.?\\.?/)*"), "")
        return basePath.resolve(safeName)
    }
}