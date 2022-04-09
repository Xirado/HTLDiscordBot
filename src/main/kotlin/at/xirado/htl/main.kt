@file:JvmName("Main")
package at.xirado.htl

import at.xirado.htl.io.FileLoader
import at.xirado.htl.io.nullOrBlank
import at.xirado.htl.listener.*
import dev.minn.jda.ktx.Embed
import dev.minn.jda.ktx.getDefaultScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.GatewayEncoding
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.dv8tion.jda.api.utils.data.DataObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.net.URL
import kotlin.time.Duration.Companion.minutes

val log = LoggerFactory.getLogger("Main") as Logger

const val DEVELOPER_ID = 184654964122058752
const val GUILD_ID = 713469621532885002
const val ONE_WORD_STORY_CHANNEL_ID = 793082837070381056
const val COUNTING_CHANNEL_ID = 714230681215041607
const val BINARY_COUNTING_CHANNEL_ID = 714837732429660170
const val MEMBER_COUNT_CHANNEL_ID = 719276192108249230
const val ONLINE_COUNT_CHANNEL_ID = 719276235129094174
const val VERIFIED_ROLE_ID = 878201479855493130

const val LOGO_URL = "https://i.imgur.com/gO4qbaz.gif"
const val RULE_CHANNEL_JUMP_URL = "https://discord.com/channels/713469621532885002/745264850950946926/802991263325749268"
const val INVITE_URL = "https://discord.gg/Ktt4pbEmeK"

val VERIFY_BUTTON = Button.primary("verify", "Verifizieren")
val RULES_BUTTON = Button.link(RULE_CHANNEL_JUMP_URL, "Hausordnung").withEmoji(Emoji.fromUnicode("\uD83D\uDCDC"))
val INVITE_BUTTON = Button.link(INVITE_URL, "Server beitreten")
val SEND_AGAIN = Button.secondary("send_again", "Erneut senden")

val DM_EMBED = Embed {
    author {
        name = "Verifizierung | HTL Discord"
        iconUrl = LOGO_URL
    }
    color = 0x3eb489
    description = "**Servus und willkommen auf dem HTL Discord-Server!**\n\nUm Zugriff auf alle Kanäle zu haben, musst du dich noch verifizieren!\nDafür brauchst du nur auf den **Verifizieren** Button drücken!"
    footer {
        name = "Mit deiner Verifizierung bestätigst du, unsere Hausordnung gelesen und verstanden zu haben!"
    }
}

val DM_DISABLED_EMBED = Embed {
    description = "Ich konnte dir keine Nachricht schicken, da du Privatnachrichten deaktiviert hast!"
    color = 0xcf1020
    author {
        name = "Verifizierung | HTL Discord"
        iconUrl = LOGO_URL
    }
}

val DM_SENT_SUCCESS_EMBED = Embed {
    description = "Du hast eine Nachricht von mir erhalten!"
    color = 0x3eb489
    author {
        name = "Verifizierung | HTL Discord"
        iconUrl = LOGO_URL
    }
}

val ERROR_OCCURRED_EMBED = Embed {
    description = "Es ist ein unbekannter Fehler aufgetreten. Bitte versuche es später erneut, oder sende eine Direktnachricht an <@$DEVELOPER_ID>!"
    color = 0xcf1020
}

val NO_LONGER_IN_GUILD_EMBED = Embed {
    description = "Du bist nicht mehr auf dem Server!"
    color = 0xcf1020
}

val ALREADY_VERIFIED_EMBED = Embed {
    description = "Du bist bereits verifiziert!"
    author {
        name = "Verifizierung | HTL Discord"
        iconUrl = LOGO_URL
    }
    color = 0xcf1020
}

val VERIFIED_SUCCESS_EMBED = Embed {
    description = "Danke, du wurdest erfolgreich verifiziert! **Wir wünschen dir viel Spaß!**"
    color = 0x3eb489
    author {
        name = "Verifizierung | HTL Discord"
        iconUrl = LOGO_URL
    }
}

val VERIFY_EMBED = Embed {
    description = "Servus und willkommen auf dem HTL Discord!\nDu solltest nach dem Beitreten eine Privatnachricht erhalten haben.\nIst das nicht der Fall, drücke bitte auf den **Erneut senden** Button.\nWenn du immernoch keine Nachricht erhalten hast, vergewissere dich, dass du DMs eingeschalten hast!\n\nFalls die Verifizierung nicht funktioniert, schreibe bitte eine DM an <@$DEVELOPER_ID>"
    color = 0x3eb489
    author {
        name = "Verifizierung | HTL Discord"
        iconUrl = LOGO_URL
    }

}

val coroutineScope = getDefaultScope()

var cachedMemberCount = 0
var cachedOnlineCount = 0

fun main() {
    val config = FileLoader.loadFileAsYaml("config.yml", true)
    if (config.nullOrBlank("token"))
        throw IllegalStateException("config.yml is missing \"token\" property!")

    JDABuilder.create(config.getString("token"), getIntents())
        .addEventListeners(
            MemberJoinListener(), ButtonListener(), MessageListener(),
            CountingListener(), BinaryCountingListener(), OneWordStoryListener(),
            ReadyEvent()
        )
        .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE)
        .setBulkDeleteSplittingEnabled(false)
        .setGatewayEncoding(GatewayEncoding.ETF)
        .build()
}

fun initializeMemberCounter(jda: JDA) {
    coroutineScope.launch {
        while (true) {
            try {
                val guild = jda.getGuildById(GUILD_ID)

                if (guild == null) {
                    log.error("Guild is unavailable!", RuntimeException("guild == null"))
                    break
                }

                val memberCount = guild.memberCount
                if (memberCount != cachedMemberCount) {
                    val channel = guild.getVoiceChannelById(MEMBER_COUNT_CHANNEL_ID)
                    channel?.manager?.setName("『🙋』Mitglieder: $memberCount")?.queue { cachedMemberCount = memberCount }
                    jda.presence.setPresence(OnlineStatus.ONLINE, Activity.watching("$memberCount Mitglieder"))
                }

                val url = URL("https://discord.com/api/guilds/$GUILD_ID/widget.json")
                val dataObject = withContext(Dispatchers.IO) {
                    DataObject.fromJson(url.openStream())
                }

                val onlineCount = dataObject.getInt("presence_count", 0)
                if (onlineCount != cachedOnlineCount) {
                    val channel = guild.getVoiceChannelById(ONLINE_COUNT_CHANNEL_ID)
                    channel?.manager?.setName("『\uD83D\uDCBB』Online: $onlineCount")?.queue { cachedOnlineCount = onlineCount }
                }
            } catch (ex: Exception) {
                log.error("An unhandled error occurred in the member counter coroutine!", ex)
            }

            delay(10.minutes)
        }
    }
}

fun getIntents(): Set<GatewayIntent> {
    return setOf(
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_PRESENCES
    )
}

fun Member.hasRole(id: Long): Boolean {
    return roles.map { it.idLong }.contains(id)
}