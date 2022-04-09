package at.xirado.htl.listener

import at.xirado.htl.GUILD_ID
import at.xirado.htl.ONE_WORD_STORY_CHANNEL_ID
import at.xirado.htl.initializeMemberCounter
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class ReadyEvent : ListenerAdapter() {

    private var ready = false

    override fun onGuildReady(event: GuildReadyEvent) {
        if (event.guild.idLong == GUILD_ID) {
            if (ready)
                return

            ready = true
            initializeMemberCounter(event.jda)
            val oneWordStory = event.jda.getTextChannelById(ONE_WORD_STORY_CHANNEL_ID)
            oneWordStory?.sendMessage("\uD83E\uDD16 Bip Bop \uD83E\uDD16 Ich wurde neugestartet und die letzte laufende Geschichte wurde beendet.")?.queue()
        }
    }

}