package me.purox.musicbot.listener

import me.purox.musicbot.commands.Command
import me.purox.musicbot.musicBot
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class CommandListener : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent?) {
        val message = event?.message?.contentRaw

        if (!event?.author?.isBot!! && message?.startsWith(musicBot.config.prefix)!!) {
            val invoke = message.substring(musicBot.config.prefix.length).replace("\n", " ").split(" ")[0].toLowerCase();

            if (musicBot.commandHandler.commands.contains(invoke)) {
                musicBot.commandHandler.handleCommand(Command(event))
            }
        }
    }
}