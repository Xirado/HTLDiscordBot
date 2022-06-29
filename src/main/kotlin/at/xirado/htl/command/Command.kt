package at.xirado.htl.command

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

abstract class Command(val name: String) {

    abstract suspend fun execute(event: MessageReceivedEvent, args: List<String>)
}