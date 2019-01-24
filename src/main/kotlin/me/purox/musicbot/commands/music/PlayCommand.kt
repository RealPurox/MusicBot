package me.purox.musicbot.commands.music

import me.purox.musicbot.commands.Command
import me.purox.musicbot.commands.ICommand
import me.purox.musicbot.music.GuildPlayer
import me.purox.musicbot.musicBot
import java.util.stream.Collectors

class PlayCommand : ICommand("play") {

    override fun execute(command: Command) {
        val guildPlayer : GuildPlayer = musicBot.musicManager.getGuildPlayer(command.guild)

        if (command.args.isEmpty()) {
            command.channel.sendMessage("Please provide a music source.").queue()
            return
        }

        var query: String = command.args.stream().skip(0).collect(Collectors.joining(" "))
        if (!command.args[0].startsWith("http")) query = "ytsearch:$query"
        guildPlayer.loadSong(query, command, command.member)
    }

}