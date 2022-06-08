package at.xirado.htl.listener

import at.xirado.htl.BINARY_COUNTING_CHANNEL_ID
import at.xirado.htl.log
import dev.minn.jda.ktx.CoroutineEventListener
import dev.minn.jda.ktx.await
import kotlinx.coroutines.delay
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import kotlin.time.Duration.Companion.seconds

class BinaryCountingListener : CoroutineEventListener {

    override suspend fun onEvent(event: GenericEvent) {
        when (event) {
            is MessageReceivedEvent -> onMessageReceived(event)
        }
    }

    private suspend fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.channel.idLong != BINARY_COUNTING_CHANNEL_ID)
            return

        val channel = event.channel

        if (event.isWebhookMessage || (event.author.isBot && event.author.idLong != event.jda.selfUser.idLong)) {
            event.message.delete().queue()
            return
        }

        if (event.author.idLong == event.jda.selfUser.idLong)
            return

        if (!event.message.contentRaw.matches("[01 ]+".toRegex())) {
            event.message.delete().await()
            val response = channel.sendMessage("${event.author.asMention}: Binär, schonmal gehört?!").await()
            delay(10.seconds)
            runCatching { response.delete().await() }
            return
        }

        val parsed = event.message.contentRaw.replace(" ", "").toLong(2)

        val history = event.channel.history.retrievePast(100).await()
        history.removeAt(0)

        val latestMessage = history.firstOrNull {
            !it.isEdited &&
            !it.author.isBot &&
            !it.isWebhookMessage &&
            it.contentRaw.matches("[01 ]+".toRegex())
        }

        if (latestMessage == null) {
            log.error("No unedited messages found in binary counting channel! (100 messages)")
            return
        }

        val latestMessageParsed = latestMessage.contentRaw.replace(" ", "").toLong(2)

        if (latestMessageParsed+1 != parsed) {
            event.message.delete().await()
            val response = channel.sendMessage("${event.author.asTag} kann nicht zählen!").await()
            delay(10.seconds)
            kotlin.runCatching { response.delete().await() }
            return
        }
    }
}