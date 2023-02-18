@file:JvmName("Main")
package at.xirado.htl

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.athena.service.ServiceManager
import org.athena.util.getLog
import org.slf4j.LoggerFactory

private val log = getLog("main")

fun main() {
    configureLogging()

    ServiceManager.registerPackage("at.xirado.htl.service")
    ServiceManager.start()

    ServiceManager.awaitShutdown()
}

private fun configureLogging() {
    val debugProperty = System.getenv("athena-debug")
    val rootLogger = LoggerFactory.getLogger("ROOT") as Logger

    rootLogger.level = Level.INFO
    if (debugProperty != null) {
        val packages = debugProperty.split("\\s+".toRegex())
        packages.forEach { pkg ->
            val logger = LoggerFactory.getLogger(pkg) as Logger
            logger.level = Level.DEBUG
            log.warn("Set level to DEBUG for logger {}", pkg)
        }
    }
}
