package at.xirado.htl.listener

import at.xirado.htl.*
import dev.minn.jda.ktx.CoroutineEventListener
import dev.minn.jda.ktx.await
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.requests.ErrorResponse

class MemberJoinListener : CoroutineEventListener {

    override suspend fun onEvent(event: GenericEvent) {
        when (event) {
            is GuildMemberJoinEvent -> onGuildMemberJoin(event)
        }
    }

    private suspend fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        if (event.user.isBot)
            return

        val user = event.user
        val channel = user.openPrivateChannel().await()
        try {
            channel.sendMessageEmbeds(DM_EMBED).setActionRow(VERIFY_BUTTON, RULES_BUTTON).await()
        } catch (ex: ErrorResponseException) {
            if (ex.errorResponse == ErrorResponse.CANNOT_SEND_TO_USER) {
                event.jda.getTextChannelById(LOG_CHANNEL_ID)
                    ?.sendMessageEmbeds(dmDisabledUponJoinLogMessage(user))
                    ?.queue()
                return
            }

            log.error("An error occurred while sending verification message to user ${user.idLong}", ex)
        }
    }
}