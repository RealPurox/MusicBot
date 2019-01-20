package me.purox.musicbot.commands.music

import me.purox.musicbot.commands.Command
import me.purox.musicbot.commands.ICommand

class TestCommand : ICommand("test", "testing") {

    override fun execute(command: Command) {
        command.event.channel.sendMessage("works").queue()
    }

}