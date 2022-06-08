package at.xirado.htl.listener

import at.xirado.htl.*
import dev.minn.jda.ktx.CoroutineEventListener
import dev.minn.jda.ktx.await
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.requests.ErrorResponse

class ButtonListener : CoroutineEventListener {

    override suspend fun onEvent(event: GenericEvent) {
        when (event) {
            is ButtonInteractionEvent -> onButtonInteraction(event)
        }
    }

    private suspend fun onButtonInteraction(event: ButtonInteractionEvent) {
        val role = event.jda.getRoleById(VERIFIED_ROLE_ID)
        when (event.interaction.componentId) {
            "send_again" -> {
                if (role == null) {
                    event.replyEmbeds(ERROR_OCCURRED_EMBED).setEphemeral(true).queue()
                    log.error("Verified Role no longer exists!")
                    return
                }
                val member = event.member
                if (role in member!!.roles) {
                    event.replyEmbeds(ALREADY_VERIFIED_EMBED).setEphemeral(true).queue()
                    return
                }

                val user = member.user
                event.deferReply(true).queue()
                try {
                    val channel = user.openPrivateChannel().await()
                    channel.sendMessageEmbeds(DM_EMBED).setActionRow(VERIFY_BUTTON, RULES_BUTTON).await()
                    event.hook.sendMessageEmbeds(DM_SENT_SUCCESS_EMBED).await()
                } catch (ex: ErrorResponseException) {
                    if (ex.errorResponse == ErrorResponse.CANNOT_SEND_TO_USER) {
                        event.hook.sendMessageEmbeds(DM_DISABLED_EMBED).await()
                        return
                    }
                    log.error("An unexpected error occurred! (User: ${user.idLong} / ${user.asTag})", ex)
                    event.hook.sendMessageEmbeds(ERROR_OCCURRED_EMBED).await()
                }
            }
            "verify" -> {
                if (role == null) {
                    event.replyEmbeds(ERROR_OCCURRED_EMBED).queue()
                    log.error("Verified Role no longer exists!")
                    return
                }
                event.deferReply().queue()
                val guild = role.guild
                try {
                    val member = guild.retrieveMember(event.user).await()
                    if (member.hasRole(VERIFIED_ROLE_ID)) {
                        event.hook.sendMessageEmbeds(ALREADY_VERIFIED_EMBED).queue()
                        return
                    }
                } catch (ex: ErrorResponseException) {
                    if (ex.errorResponse == ErrorResponse.UNKNOWN_MEMBER) {
                        event.hook.sendMessageEmbeds(NO_LONGER_IN_GUILD_EMBED).addActionRow(INVITE_BUTTON).await()
                        return
                    } else {
                        event.hook.sendMessageEmbeds(ERROR_OCCURRED_EMBED).await()
                        log.error("An unexpected error occurred!", ex)
                        return
                    }
                }
                try {
                    guild.addRoleToMember(event.user.idLong, role).await()
                    event.hook.sendMessageEmbeds(VERIFIED_SUCCESS_EMBED).await()
                } catch (ex: Exception) {
                    event.hook.sendMessageEmbeds(ERROR_OCCURRED_EMBED).await()
                    log.error("An unexpected error occurred!", ex)
                }
            }
        }
    }
}