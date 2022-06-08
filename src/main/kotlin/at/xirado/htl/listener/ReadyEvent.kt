package at.xirado.htl.listener

import at.xirado.htl.GUILD_ID
import at.xirado.htl.ONE_WORD_STORY_CHANNEL_ID
import at.xirado.htl.initializeMemberCounter
import dev.minn.jda.ktx.CoroutineEventListener
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import java.util.concurrent.atomic.AtomicBoolean

class ReadyEvent : CoroutineEventListener {

    private var ready = AtomicBoolean(false)

    override suspend fun onEvent(event: GenericEvent) {
        when (event) {
            is GuildReadyEvent -> onGuildReady(event)
        }
    }

    private fun onGuildReady(event: GuildReadyEvent) {
        if (event.guild.idLong == GUILD_ID && ready.compareAndSet(false, true))
            onReady(event)
    }

    private fun onReady(event: GuildReadyEvent) {
        initializeMemberCounter(event.jda)
        val oneWordStory = event.jda.getTextChannelById(ONE_WORD_STORY_CHANNEL_ID)
        oneWordStory?.sendMessage("\uD83E\uDD16 Bip Bop \uD83E\uDD16 Ich wurde neugestartet und die letzte laufende Geschichte wurde beendet.")?.queue()
    }

}