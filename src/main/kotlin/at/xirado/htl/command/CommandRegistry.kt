package at.xirado.htl.command

import at.xirado.htl.command.commands.MetaQuestionCommand
import dev.minn.jda.ktx.CoroutineEventListener
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.util.concurrent.ConcurrentHashMap

private const val PREFIX = "%"

class CommandRegistry : CoroutineEventListener {

    private val commands = ConcurrentHashMap<String, Command>()

    init {
        registerCommands()
    }

    private fun registerCommands() {
        registerCommand(MetaQuestionCommand())
    }

    private fun registerCommand(command: Command) {
        commands[command.name.lowercase()] = command
    }

    override suspend fun onEvent(event: GenericEvent) {
        if (event is MessageReceivedEvent) {
            if (event.author.isBot || event.isWebhookMessage)
                return

            if (!event.message.contentRaw.startsWith(PREFIX))
                return

            val split = event.message.contentRaw.split("\\s+".toRegex())
            val commandName = split[0].removePrefix(PREFIX).lowercase()
            val args = if (split.size > 1) split.subList(1, split.lastIndex) else emptyList()
            if (commandName in commands.keys)
                commands[commandName]!!.execute(event, args)
        }
    }
}