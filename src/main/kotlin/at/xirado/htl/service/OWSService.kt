package at.xirado.htl.service

import at.xirado.htl.db.entity.OWSHistoryEntry
import at.xirado.htl.db.table.owsHistoryEntries
import at.xirado.htl.utils.*
import dev.minn.jda.ktx.coroutines.await
import kotlinx.coroutines.delay
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.TimeUtil
import org.athena.service.JDAEventService
import org.ktorm.entity.add
import org.ktorm.entity.clear
import org.ktorm.entity.toList
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

class OWSService : HTLService, JDAEventService {
    override val requiredIntents: EnumSet<GatewayIntent> =
        EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)

    private val channelId = config.ows.channel
    private val allowMultiple = config.ows.allowMultiple ?: false
    private val requiredRole = config.ows.endRequiredRole
    private val commentExample = "> Hier ist ein cooler Kommentar\n`> Dies ist ein cooler Kommentar`"

    private val currentStory: MutableList<OWSHistoryEntry> = Collections.synchronizedList(mutableListOf())
    private var lastStart = AtomicReference(OffsetDateTime.now())
    private var lastMember = AtomicLong(0)

    override suspend fun onEvent(event: GenericEvent) {
        if (event is MessageReceivedEvent) {
            if (event.channel.idLong != channelId)
                return

            if (event.isWebhookMessage || event.author.isBot)
                return

            val content = event.message.contentRaw
            if (content.startsWith("//")) {
                val response =
                    event.message.reply("Kommentare werden nicht mehr mit `//`, sondern mit `> ` gestartet.\n\n$commentExample")
                        .mentionRepliedUser(true)
                        .await()

                delay(5000)
                response.delete().queueSilently()
                event.message.delete().queueSilently()
                return
            }

            if (content.startsWith("> "))
                return

            onMessage(event)
        }
    }

    private suspend fun onMessage(event: MessageReceivedEvent) {
        val content = event.message.contentRaw
        val member = event.member!!

        if (content.equals("!geschichte", true)) {
            event.message.delete().await()
            sendHistory(true, event.author.idLong, event.jda)
            return
        }

        if (content.equals("!ende", true)) {
            if (member.hasRole(requiredRole) || member.hasPermission(Permission.ADMINISTRATOR))
                sendHistory(false, 0, event.jda)
            else
                event.message.delete().await()
            return
        }

        if (content.startsWith("!")) {
            event.message.delete().await()
            return
        }

        if (!allowMultiple && lastMember.get() == event.member!!.idLong) {
            event.message.delete().await()
            return
        }

        owsRegex.matchEntire(content) ?: run {
            event.message.delete().await()
            return
        }

        lastMember.set(event.member!!.idLong)

        val owsHistoryEntry = OWSHistoryEntry {
            this.messageId = event.messageIdLong
            this.authorId = event.member!!.idLong
            this.content = content
        }

        db.owsHistoryEntries.add(owsHistoryEntry)

        currentStory.add(owsHistoryEntry)
    }

    private suspend fun sendHistory(inPrivateMessage: Boolean, userId: Long, jda: JDA) {
        val channel = jda.getTextChannelById(channelId) ?: return

        val history = channel.iterableHistory

        val content =
            if (currentStory.isEmpty()) "" else history.takeWhileAsync { it.timeCreated.isAfter(lastStart.get()) || it.timeCreated.isEqual(lastStart.get()) }
                .await()
                .filter { msg -> currentStory.any { it.messageId == msg.idLong } }
                .map(Message::getContentRaw)
                .map(::parseOWSMessage)
                .reversed()
                .merge()

        if (content.isEmpty()) {
            if (inPrivateMessage) {
                val dm = jda.openPrivateChannelById(userId).await()
                kotlin.runCatching { dm.sendMessage("**Es gibt noch keine Geschichte...**").await() }
            } else {
                channel.sendMessage("**Bruh, die Geschichte hat noch nicht einmal angefangen...**").await()
            }
            return
        }

        if (inPrivateMessage) {
            val dm = jda.openPrivateChannelById(userId).await()
            kotlin.runCatching { dm.sendMessage("**Zusammenfassung der laufenden Geschichte:**\n\n$content").await() }
        } else {
            channel.sendMessage("**Eure Geschichte:**\n\n$content")
                .setAllowedMentions(emptySet())
                .await()
            currentStory.clear()
            lastStart.set(OffsetDateTime.now())
            lastMember.set(0)
        }
    }

    override suspend fun onLoad() {
        currentStory += db.owsHistoryEntries.toList()
        if (currentStory.isNotEmpty()) {
            lastStart.set(TimeUtil.getTimeCreated(currentStory.minOf { it.messageId }))
            lastMember.set(currentStory.maxByOrNull { it.messageId }!!.authorId)
        }

        if (allowMultiple)
            log.warn("[OWS] allowMultiple ENABLED!")

        db.owsHistoryEntries.clear()
    }

    override suspend fun onUnload() {
        super.onUnload()
    }
}

data class OWSMessage(
    val prefix: Char?,
    val word: String,
    val suffix: Char?
) {
    val connectsWithPreviousMessage = prefix == '-'
    val connectsWithNextMessage = suffix == '-'
}