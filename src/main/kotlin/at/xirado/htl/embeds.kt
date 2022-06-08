package at.xirado.htl

import dev.minn.jda.ktx.Embed
import net.dv8tion.jda.api.entities.User

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

fun dmDisabledUponJoinLogMessage(user: User) = Embed {
    description = "${user.asMention} hat Direktnachrichten deaktiviert und konnte deshalb nicht verifiziert werden."
    color = 0xcf1020
    author {
        name = "Verifizierung | HTL Discord"
        iconUrl = LOGO_URL
    }
}