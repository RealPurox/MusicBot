package me.purox.musicbot.commands.music

import me.purox.musicbot.commands.Command
import me.purox.musicbot.commands.CommandSender
import me.purox.musicbot.commands.ICommand
import me.purox.musicbot.music.GuildPlayer
import me.purox.musicbot.musicBot

class SkipCommand : ICommand("skip") {

    override fun execute(command: Command, sender: CommandSender) {
        val guildPlayer : GuildPlayer = musicBot.musicManager.getGuildPlayer(command.guild)

        if (guildPlayer.audioPlayer.isPaused) {
            sender.error("The music player is currently paused")
            return
        }

        if (guildPlayer.audioPlayer.playingTrack == null) {
            sender.error("The music player is currently inactive")
            return
        }

        var amount : Int = if (command.args.isEmpty()) 1 else command.args[0].toInt()
        if (amount < 1) amount = 1

        val skipped = guildPlayer.skipSongs(amount)

        sender.success("Successfully skipped __${skipped}__ songs. The queue has now __${guildPlayer.queue.size}__ songs left.")
    }
}
