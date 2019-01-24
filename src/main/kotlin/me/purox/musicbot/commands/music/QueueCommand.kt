package me.purox.musicbot.commands.music

import me.purox.musicbot.Emote
import me.purox.musicbot.commands.Command
import me.purox.musicbot.commands.ICommand
import me.purox.musicbot.music.AudioInfo
import me.purox.musicbot.music.GuildPlayer
import me.purox.musicbot.musicBot
import net.dv8tion.jda.core.EmbedBuilder

class QueueCommand : ICommand("queue") {

    override fun execute(command: Command) {
        val guildPlayer : GuildPlayer = musicBot.musicManager.getGuildPlayer(command.guild)
        val builder : EmbedBuilder = EmbedBuilder()
                .setColor(0x36393E)
                .setAuthor("${command.guild.name} - Music Queue")
                .appendDescription("")

        var displayNext = false

        if (guildPlayer.audioPlayer.isPaused) {
            builder.appendDescription("${Emote.ERROR} | The music player is currently paused")
        } else if (guildPlayer.queue.peek() == null || !guildPlayer.queue.peek().playing) {
            builder.appendDescription("${Emote.ERROR} | The music player is not playing music at the moment")
        } else {
            if (guildPlayer.queue.size > 1) displayNext = true
            val current : AudioInfo = guildPlayer.queue.peek()

            builder.appendDescription(":musical_note: __**Currently Playing**__ :musical_note:\n\n")
            builder.appendDescription("`${guildPlayer.getAudioInfoId(current)}` [${current.audioTrack.info.title}](${current.audioTrack.info.uri}) - requested by **${current.requester.name}#${current.requester.discriminator}**\n\n")
        }

        if (displayNext) {
            val list : MutableList<AudioInfo> = guildPlayer.getNextSongs(amount = 10)
            builder.appendDescription(":arrow_double_down: __**Up Next**__ :arrow_double_down:\n\n")

            for (audioInfo in list) {
                builder.appendDescription("`${guildPlayer.getAudioInfoId(audioInfo)}` [${audioInfo.audioTrack.info.title}](${audioInfo.audioTrack.info.uri}) - requested by **${audioInfo.requester.name}#${audioInfo.requester.discriminator}**\n\n")
            }
        }

        builder.setFooter("${guildPlayer.queue.size} songs: ${guildPlayer.getQueueDuration().replace("`", "")}", null)
        command.channel.sendMessage(builder.build()).queue()
    }
}
