package at.xirado.htl.utils

import at.xirado.htl.service.OWSMessage
import net.dv8tion.jda.api.requests.RestAction
import java.lang.StringBuilder

fun RestAction<*>.queueSilently() = queue({ }, { })

val owsRegex = """^([.,;?!] |-|)?([^\s_.,;?!]+)(?:([.,;?!]|-|) ?)?$""".toRegex()

fun parseOWSMessage(message: String): OWSMessage {
    val match = owsRegex.matchEntire(message)
        ?: throw IllegalArgumentException("String \"$message\" does not match regex!")

    val (prefix, word, suffix) = match.destructured


    val prefixChar = if (prefix.isNotEmpty()) prefix.trim()[0] else null
    val suffixChar = if (suffix.isNotEmpty()) suffix.trim()[0] else null

    return OWSMessage(prefixChar, word, suffixChar)
}

fun List<OWSMessage>.merge(): String {
    val sb = StringBuilder()

    forEachIndexed { index, msg ->
        val next = if (index == lastIndex) null else this[index + 1]

        if (msg.prefix != null && !msg.connectsWithPreviousMessage) {
            sb.append("${msg.prefix} ")
        }

        sb.append(msg.word)

        if (msg.suffix != null && !msg.connectsWithNextMessage)
            sb.append(msg.suffix)

        if (next != null && !msg.connectsWithNextMessage) {
            if (next.prefix == null)
                sb.append(" ")
        }
    }

    return sb.toString()
}