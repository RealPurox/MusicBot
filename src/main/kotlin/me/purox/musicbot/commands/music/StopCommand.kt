package me.purox.musicbot.commands.music

import me.purox.musicbot.commands.Command
import me.purox.musicbot.commands.ICommand
import me.purox.musicbot.music.GuildPlayer
import me.purox.musicbot.musicBot

class StopCommand : ICommand("stop", "leave", "exit") {

    override fun execute(command: Command) {
        val guildPlayer : GuildPlayer = musicBot.musicManager.getGuildPlayer(command.guild)
        guildPlayer.destroy()
        command.channel.sendMessage(":wave: Goodbye!").queue()
    }
}
