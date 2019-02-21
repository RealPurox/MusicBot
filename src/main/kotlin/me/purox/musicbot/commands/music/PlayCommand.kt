package me.purox.musicbot.commands.music

import me.purox.musicbot.Emote
import me.purox.musicbot.commands.Command
import me.purox.musicbot.commands.CommandSender
import me.purox.musicbot.commands.ICommand
import me.purox.musicbot.music.GuildPlayer
import me.purox.musicbot.musicBot
import java.util.function.Consumer
import java.util.stream.Collectors

class PlayCommand : ICommand("play") {

    override fun execute(command: Command, sender: CommandSender) {
        val guildPlayer : GuildPlayer = musicBot.musicManager.getGuildPlayer(command.guild)

        if (command.args.isEmpty()) {
            sender.error("Please provide a music source. `${musicBot.config.prefix}play <link or title>`")
            return
        }

        sender.reply("(${Emote.SUCCESS}): Loading songs.. ${Emote.LOADING}", Consumer {message ->
            run {
                var query: String = command.args.stream().skip(0).collect(Collectors.joining(" "))
                if (!command.args[0].startsWith("http")) query = "ytsearch:$query"
                guildPlayer.loadSong(query, command, sender, message)
            }
        })

    }

}