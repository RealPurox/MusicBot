package me.purox.musicbot.commands.music

import me.purox.musicbot.commands.Command
import me.purox.musicbot.commands.CommandSender
import me.purox.musicbot.commands.ICommand
import me.purox.musicbot.music.GuildPlayer
import me.purox.musicbot.musicBot

class ShuffleCommand : ICommand("shuffle") {

    override fun execute(command: Command, sender: CommandSender) {
        val guildPlayer : GuildPlayer = musicBot.musicManager.getGuildPlayer(command.guild)

        if (guildPlayer.queue.size <= 1) {
            sender.error("There are not enough songs in the queue!")
            return
        }

        guildPlayer.shuffleQueue()
        sender.success("The queue has been shuffled!")
    }

}
