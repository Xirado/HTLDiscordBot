package at.xirado.htl.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.athena.service.ConfigurationProvider

object HTLConfigProvider : ConfigurationProvider<HTLConfig> {
    override val serializer: KSerializer<HTLConfig> = HTLConfig.serializer()
    override val prefix: String = "htl"
}

@Serializable
data class HTLConfig(
    val developer: Long,
    val guild: Long,
    val channels: HTLChannels,
    val ows: OneWordStory,
    val roles: HTLRoles,
    @SerialName("logo_url")
    val logoUrl: String,
    @SerialName("rule_channel_jump_url")
    val ruleChannelJumpUrl: String,
    @SerialName("invite_url")
    val inviteUrl: String,
)

@Serializable
data class OneWordStory(
    val channel: Long,
    @SerialName("allow_multiple")
    val allowMultiple: Boolean? = false,
    @SerialName("end_required_role")
    val endRequiredRole: Long
)

@Serializable
data class HTLChannels(
    val counting: Long,
    @SerialName("binary_counting")
    val binaryCounting: Long,
    @SerialName("member_count")
    val memberCount: Long,
    @SerialName("online_count")
    val onlineCount: Long,
    val logs: Long? = null,
)

@Serializable
data class HTLRoles(
    val verified: Long,
)
