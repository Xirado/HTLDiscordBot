package at.xirado.htl.listener

import at.xirado.htl.GUILD_ID
import at.xirado.htl.SEND_AGAIN
import at.xirado.htl.VERIFY_EMBED
import at.xirado.htl.coroutineScope
import dev.minn.jda.ktx.EmbedBuilder
import dev.minn.jda.ktx.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.internal.requests.restaction.MessageActionImpl
import java.awt.Color

class MessageListener : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (!event.isFromGuild)
            return
        if (event.guild.idLong != GUILD_ID)
            return

        if (!event.member!!.hasPermission(Permission.ADMINISTRATOR))
            return

        val content = event.message.contentRaw

        coroutineScope.launch {
            if (content.equals("\$sendverifymessage", true)) {
                event.message.delete().queue()
                event.channel.sendMessageEmbeds(VERIFY_EMBED).setActionRow(SEND_AGAIN).queue()
            }
            if (content.startsWith("\$embed", true)) {

                val args = content.split("\\s+".toRegex())
                if (args.size == 1) {
                    event.message.reply("Was willst du einbetten du Hund").queue()
                    return@launch
                }
                val message = event.message
                val embed = EmbedBuilder {
                    color = Color.magenta.rgb
                    description = args.subList(1, args.size).joinToString(" ")
                }
                val action = MessageActionImpl(event.jda, null, event.channel)
                if (message.attachments.size >= 1) {
                    embed.image = "attachment://${message.attachments[0].fileName}"
                    withContext(Dispatchers.IO) {
                        action.addFile(message.attachments[0].retrieveInputStream().await(), message.attachments[0].fileName)
                    }
                }
                action.setEmbeds(embed.build())
                action.queue()
                event.message.delete().queue()
            }
        }
    }
}