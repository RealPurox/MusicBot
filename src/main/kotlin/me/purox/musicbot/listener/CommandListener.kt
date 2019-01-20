package me.purox.musicbot.listener

import me.purox.musicbot.COMMAND_HANDLER
import me.purox.musicbot.PREFIX
import me.purox.musicbot.commands.Command
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class CommandListener : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent?) {
        val message = event?.message?.contentRaw

        if (!event?.author?.isBot!! && message?.startsWith(PREFIX)!!) {
            val invoke = message.substring(PREFIX.length).replace("\n", " ").split(" ")[0].toLowerCase();

            if (COMMAND_HANDLER.commands.contains(invoke)) {
                COMMAND_HANDLER.handleCommand(Command(event))
            }
        }
    }
}