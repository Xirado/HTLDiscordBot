package at.xirado.htl.command.commands

import at.xirado.htl.command.Command
import dev.minn.jda.ktx.Embed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

private val DESCRIPTION = """
    Eine Metafrage ist eine Frage über eine Frage,
    wie beispielsweise „Darf ich etwas fragen?“ oder „Kennt sich jemand mit
    Computern aus?“.
    {line}
    In der Regel wird der Begriff Metafrage aber verallgemeinert und damit
    alle Fragen bezeichnet, die keine direkte Frage zum Problem des
    Hilfesuchenden sind. Der Hilfesuchende fragt also zunächst allgemein,
    ob jemand helfen kann. Gerade Neulinge oder unerfahrene Benutzer
    lassen sich zu Metafragen hinreißen, um einen kompetenten
    und hilfsbereiten Ansprechpartner zu finden. Meistens 
    werden Metafragen ignoriert oder der Fragende wird rüde darauf
    hingewiesen, dass ihm niemand bei seinem Problem helfen könne,
    ohne dies zu kennen. Grundsätzlich folgt auf eine Meta-Frage
    eine weitere Frage.
    {line}
    [Mehr lesen](http://metafrage.de/)
""".trimIndent().replace('\n', ' ').replace("{line}", "\n\n")

class MetaQuestionCommand : Command("metafrage") {
    override suspend fun execute(event: MessageReceivedEvent, args: List<String>) {
        val embed = Embed {
            description = DESCRIPTION
            color = 0xcf1020
        }

        event.channel.sendMessageEmbeds(embed)
            .content("\uD83D\uDEAB Metafrage-Alarm \uD83D\uDEAB")
            .queue()
    }
}