package me.purox.musicbot.commands

import me.purox.musicbot.COMMAND_HANDLER
import me.purox.musicbot.PREFIX
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

open class Command(val event: MessageReceivedEvent) : MessageReceivedEvent(event.jda, event.responseNumber, event.message) {

    var invoke: String
    var raw: String
    var args: List<String>
    var iCommand: ICommand?

    init {
        val split = event.message.contentRaw.substring(PREFIX.length).split(" ")

        invoke = split[0]
        raw = event.message.contentRaw
        args = split.subList(1, split.size)
        iCommand = COMMAND_HANDLER.commands[invoke]
    }
}