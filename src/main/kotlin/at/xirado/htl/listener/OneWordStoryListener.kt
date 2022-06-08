package at.xirado.htl.listener

import at.xirado.htl.ONE_WORD_STORY_CHANNEL_ID
import at.xirado.htl.hasRole
import dev.minn.jda.ktx.CoroutineEventListener
import dev.minn.jda.ktx.await
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.time.OffsetDateTime
import java.util.*

class OneWordStoryListener : CoroutineEventListener {

    val currentStory: MutableList<Long> = Collections.synchronizedList(mutableListOf())
    var lastStart = OffsetDateTime.now()
    var lastMember: Long = 0

    override suspend fun onEvent(event: GenericEvent) {
        when (event) {
            is MessageReceivedEvent -> onMessageReceived(event)
        }
    }

    private suspend fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.channel.idLong != ONE_WORD_STORY_CHANNEL_ID)
            return

        if (event.isWebhookMessage)
            return

        if (event.author.idLong == event.jda.selfUser.idLong)
            return

        if (event.author.isBot) {
            event.message.delete().await()
            return
        }

        val content = event.message.contentRaw
        val member = event.member!!
        if (content.equals("!geschichte", true)) {
            event.message.delete().await()
            sendHistory(true, event.author.idLong, event.jda)
            return
        }

        if (content.equals("!ende", true)) {
            if (member.hasRole(749958593113227275) || member.hasRole(713478147634626583) || member.hasPermission(Permission.ADMINISTRATOR))
                sendHistory(false, 0, event.jda)
            else
                event.message.delete().await()
            return
        }

        if (content.startsWith("!")) {
            event.message.delete().await()
            return
        }

        if (lastMember == event.member!!.idLong) {
            event.message.delete().await()
            return
        }

        if(content.contains("\n") || content.contains("\r") || content.contains("\u200B") || content.contains("\u001b")) {
            event.message.delete().await()
            return
        }
        val args = content.split("\\s+")

        if (args.size > 1) {
            if (args.size > 2) {
                event.message.delete().await()
                return
            }
            if (content.startsWith(", ") || content.startsWith(". ")) {
                currentStory.add(event.messageIdLong)
                lastMember = event.member!!.idLong
            } else {
                event.message.delete().await()
            }
            return
        }
        lastMember = event.member!!.idLong
        currentStory.add(event.messageIdLong)
    }

    private suspend fun sendHistory(inPrivateMessage: Boolean, userId: Long, jda: JDA) {
        val channel = jda.getTextChannelById(ONE_WORD_STORY_CHANNEL_ID)?: return

        if (currentStory.size == 0) {
            if (inPrivateMessage) {
                val dm = jda.openPrivateChannelById(userId).await()
                kotlin.runCatching { dm.sendMessage("**Es gibt noch keine Geschichte...**").await() }
            } else {
                channel.sendMessage("**Bruh, die Geschichte hat noch nicht einmal angefangen...**").await()
            }
            return
        }

        val history = channel.iterableHistory

        val messages = history.takeWhile { it.timeCreated.isAfter(lastStart) }

        val stringBuilder = StringBuilder()
        val contents = mutableListOf<String>()
        messages.forEach {
            val content = it.contentRaw
            val args = content.split("\\s+".toRegex())
            if (content.startsWith("//") || content.startsWith("!") || it.author.isBot || it.isWebhookMessage)
                return@forEach
            if (content.contains("\n") || content.contains("\r") || content.contains("\u200B") || content.contains("\u001b"))
                return@forEach

            if (args.size > 1) {
                if (args.size == 2) {
                    if (content.startsWith(", ") || content.startsWith(". ")) {
                        contents.add(content)
                        return@forEach
                    }
                }
                return@forEach
            }
            if (args[0].startsWith(",")) {
                val count = args[0].chars().filter { it == ','.code }.count().toInt()
                if (count == 1) {
                    val word = args[0].substring(1)
                    contents.add(", $word")
                } else {
                    contents.add(content)
                }
                return@forEach
            } else if (args[0].startsWith(".")) {
                val count = args[0].chars().filter { it == '.'.code }.count().toInt()
                if (count == 1) {
                    val word = args[0].substring(1)
                    contents.add(". $word")
                } else {
                    contents.add(content)
                }
                return@forEach
            }
            contents.add(content)
        }
        if (contents.size == 0) {
            if (inPrivateMessage) {
                val dm = jda.openPrivateChannelById(userId).await()
                kotlin.runCatching { dm.sendMessage("**Es gibt noch keine Geschichte...**").await() }
            } else {
                channel.sendMessage("**Bruh, die Geschichte hat noch nicht einmal angefangen...**").await()
            }
            return
        }

        contents.reverse()
        contents.forEachIndexed { i, current ->
            val nextMessage = if (i+1 >= contents.size) null else contents[i+1]
            stringBuilder.append(current)
            if (nextMessage != null) {
                if (!nextMessage.startsWith(",") && !nextMessage.startsWith("."))
                    stringBuilder.append(" ")
            }
        }

        val parsedString = stringBuilder.toString().trim().replace(" -", "").replace("- ", "")
        if (inPrivateMessage) {
            val dm = jda.openPrivateChannelById(userId).await()
            kotlin.runCatching { dm.sendMessage("**Zusammenfassung der laufenden Geschichte:**\n\n$parsedString").await() }
        } else {
            channel.sendMessage("**Eure Geschichte:**\n\n$parsedString").allowedMentions(emptySet()).await()
            currentStory.clear()
            lastStart = OffsetDateTime.now()
            lastMember = 0
        }
    }
}