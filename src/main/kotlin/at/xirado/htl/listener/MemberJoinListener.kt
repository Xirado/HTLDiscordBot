package at.xirado.htl.listener

import at.xirado.htl.coroutineScope
import at.xirado.htl.DM_EMBED
import at.xirado.htl.RULES_BUTTON
import at.xirado.htl.VERIFY_BUTTON
import dev.minn.jda.ktx.await
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.hooks.ListenerAdapter

class MemberJoinListener : ListenerAdapter() {

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        if (event.user.isBot)
            return

        coroutineScope.launch {
            val user = event.user
            val channel = user.openPrivateChannel().await()
            try {
                channel.sendMessageEmbeds(DM_EMBED).setActionRow(VERIFY_BUTTON, RULES_BUTTON).await()
            } catch (_: ErrorResponseException) {}
        }
    }
}