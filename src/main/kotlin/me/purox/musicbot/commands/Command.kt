package me.purox.musicbot.commands

import me.purox.musicbot.musicBot
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

open class Command(val event: MessageReceivedEvent) : MessageReceivedEvent(event.jda, event.responseNumber, event.message) {

    var invoke: String
    var raw: String
    var args: List<String>
    var iCommand: ICommand?

    init {
        val split = event.message.contentRaw.substring(musicBot.config.prefix.length).split(" ")

        invoke = split[0]
        raw = event.message.contentRaw
        args = split.subList(1, split.size)
        iCommand = musicBot.commandHandler.commands[invoke]
    }
}