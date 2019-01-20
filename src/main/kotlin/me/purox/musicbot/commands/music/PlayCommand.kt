package me.purox.musicbot.commands.music

import me.purox.musicbot.MUSIC_MANAGER
import me.purox.musicbot.commands.Command
import me.purox.musicbot.commands.ICommand
import java.util.stream.Collectors

class PlayCommand : ICommand("play") {

    override fun execute(command: Command) {
        var guildPlayer = MUSIC_MANAGER.getGuildPlayer(command.guild)

        if (command.args.isEmpty()) {
            command.channel.sendMessage("Please provide a music source.").queue()
            return
        }

        var query: String = command.args.stream().skip(0).collect(Collectors.joining(" "))
        if (!command.args[0].startsWith("http")) query = "ytsearch:$query"
        guildPlayer!!.loadSong(query, command, command.member)

        command.channel.sendMessage("Searching song ..").queue()
    }

}