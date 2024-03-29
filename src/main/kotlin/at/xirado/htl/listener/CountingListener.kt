package at.xirado.htl.listener

import at.xirado.htl.COUNTING_CHANNEL_ID
import at.xirado.htl.log
import dev.minn.jda.ktx.CoroutineEventListener
import dev.minn.jda.ktx.await
import kotlinx.coroutines.delay
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.internal.utils.Helpers
import kotlin.time.Duration.Companion.seconds

class CountingListener : CoroutineEventListener {

    override suspend fun onEvent(event: GenericEvent) {
        when (event) {
            is MessageReceivedEvent -> onMessageReceived(event)
        }
    }

    private suspend fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.channel.idLong != COUNTING_CHANNEL_ID)
            return

        val channel = event.channel

        if (event.isWebhookMessage || (event.author.isBot && event.author.idLong != event.jda.selfUser.idLong)) {
            event.message.delete().queue()
            return
        }

        if (event.author.idLong == event.jda.selfUser.idLong)
            return

        val parsed = try {
            event.message.contentRaw.toLong()
        } catch (ex: NumberFormatException) {
            event.message.delete().await()
            val response = channel.sendMessage("${event.author.asMention}: Versuchs mal mit Zahlen!").await()
            delay(10.seconds)
            kotlin.runCatching { response.delete().await() }
            return
        }

        val history = event.channel.history.retrievePast(100).await()
        history.removeAt(0)

        val latestMessage = history.firstOrNull {
            !it.isEdited &&
            !it.author.isBot &&
            !it.isWebhookMessage &&
            Helpers.isNumeric(it.contentRaw)
        }

        if (latestMessage == null) {
            log.error("No unedited messages found in counting channel! (100 messages)")
            return
        }

        val latestMessageParsed = latestMessage.contentRaw.toLong()

        if (latestMessageParsed+1 != parsed) {
            event.message.delete().await()
            val response = channel.sendMessage("${event.author.asTag} kann nicht zählen!").await()
            delay(10.seconds)
            kotlin.runCatching { response.delete().await() }
            return
        }
    }
}