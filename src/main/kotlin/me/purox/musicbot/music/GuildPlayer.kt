package me.purox.musicbot.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import me.purox.musicbot.Emote
import me.purox.musicbot.MusicBot
import me.purox.musicbot.commands.Command
import me.purox.musicbot.commands.CommandSender
import me.purox.musicbot.musicBot
import net.dv8tion.jda.core.entities.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

class GuildPlayer(guild: Guild, musicBot: MusicBot) : AudioEventAdapter() {

    private val logger : Logger = LoggerFactory.getLogger(GuildPlayer::class.java)

    private var guildId: String = guild.id

    var destroyTime = System.currentTimeMillis() + 60000 //60 seconds
    val audioPlayer: AudioPlayer = musicBot.musicManager.audioPlayerManager.createPlayer()

    val queue : LinkedBlockingQueue<AudioInfo> = LinkedBlockingQueue()

    init {
        audioPlayer.addListener(this)
        guild.audioManager.sendingHandler = AudioPlayerSendHandler(audioPlayer)
    }

    fun addToQueue(audioInfo: AudioInfo) {
        logger.debug("Added song to queue for guild $guildId: $audioInfo")
        //already in queue
        audioInfo.queuePos = queue.size + 1
        queue.add(audioInfo)
        if (!queue.peek().playing) {
            audioPlayer.playTrack(queue.peek().audioTrack)
        }
    }

    fun getAudioInfoByPosition(pos: Int) : AudioInfo? {
        return queue.stream().filter { info -> info.queuePos == pos }.findFirst().orElseGet(null)
    }

    fun getQueueDuration() : String {
        return musicBot.musicManager.getPlayTime(queue.stream().mapToLong { info -> info.audioTrack.duration }.sum())
    }

    fun getNextSongs(amount : Int) : MutableList<AudioInfo> {
        val songs : MutableList<AudioInfo> = mutableListOf()

        for (i in 1..amount) {
            if (i >= queue.size) break

            val info = queue.toList()[i]
            var add = true

            for (song in songs) {
                if (song.isEqualTo(info)) add = false
            }

            if (add) songs.add(info)
        }

        return songs
    }

    fun skipSongs(amount: Int) : Int{
        var skipped = 0
        var fixedAmount = amount

        if (amount > queue.size)
            fixedAmount = queue.size

        for (i in 0 until fixedAmount) {
            queue.remove()
            skipped++
        }

        audioPlayer.stopTrack()

        return skipped
    }

    fun shuffleQueue() {
        val current = queue.poll()
        val list = queue.toMutableList()
        list.shuffle()
        queue.clear()
        queue.add(current)
        queue.addAll(list)
    }

    private fun getAudioInfo(track: AudioTrack) : AudioInfo? {
        return queue.stream().filter { info -> info.audioTrack == track }.findFirst().orElse(null)
    }

    fun destroy() {
        leave()
        audioPlayer.removeListener(this)
        audioPlayer.destroy()
        musicBot.musicManager.guildPlayers.remove(guildId)
    }

    fun join(voiceChannel: VoiceChannel) {
        musicBot.musicManager.executor.submit {
            val guild = musicBot.jda.getGuildById(guildId) ?: return@submit

            val audioManager = guild.audioManager

            val deviVoiceState = guild.selfMember.voiceState

            if (voiceChannel.guild.selfMember.voiceState.inVoiceChannel()) {
                if (voiceChannel.id == deviVoiceState.channel.id) {
                    return@submit
                }
                if (audioPlayer.playingTrack != null) {
                    return@submit
                }
                audioManager.closeAudioConnection()
            }

            audioManager.isAutoReconnect = true
            audioManager.openAudioConnection(voiceChannel)
            logger.info("Joined VoiceChannel $voiceChannel in guild $guildId")
        }
    }

    private fun leave() {
        musicBot.musicManager.executor.submit {
            val guild = musicBot.jda.getGuildById(guildId) ?: return@submit

            val vState = guild.selfMember.voiceState
            if (!vState.inVoiceChannel()) return@submit

            val audioManager = guild.audioManager

            audioManager.sendingHandler = null
            audioManager.isAutoReconnect = false
            audioManager.closeAudioConnection()
            logger.info("Joined VoiceChannel in guild $guildId")
        }
    }

    override fun onTrackStart(player: AudioPlayer, track: AudioTrack) {
        logger.info("Now playing ${getAudioInfo(track)} in guild $guildId")
        destroyTime = System.currentTimeMillis() + track.info.length + 600000 //60 seconds
        val audioInfo = getAudioInfo(track)
        audioInfo?.playing = true
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        //was skipped
        if (endReason == AudioTrackEndReason.STOPPED) {
            val next : AudioInfo = queue.peek() ?: return
            audioPlayer.playTrack(next.audioTrack)
            //update queue positions bc multiple songs might have been skipped
            queue.forEachIndexed { index, audioInfo ->  audioInfo.queuePos = index + 1}
            return
        }

        val endedInfo = queue.poll()
        if (endedInfo != null) endedInfo.playing = false

        val next : AudioInfo = queue.peek() ?: return
        audioPlayer.playTrack(next.audioTrack)

        //update queue positions for all tracks
        queue.forEach { info -> info.queuePos -- }
    }

    fun loadSong(query: String, command: Command, sender: CommandSender, message: Message) {
        musicBot.musicManager.audioPlayerManager.loadItem(query, object : AudioLoadResultHandler {

            override fun trackLoaded(audioTrack: AudioTrack) {
                if (!command.event.guild.selfMember.voiceState.inVoiceChannel()) {
                    if (!command.member.voiceState.inVoiceChannel()) return
                    join(command.member.voiceState.channel)
                }
                addToQueue(AudioInfo(audioTrack, "${sender.name}#${sender.discriminator}", command.event.textChannel.id))
                message.editMessage("(${Emote.SUCCESS}): ${audioTrack.info.title}` by __${audioTrack.info.author}__ has been added to the queue. ${musicBot.musicManager.getPlayTime(audioTrack.duration)}").queue()
                logger.info("Loaded Track ${audioTrack.info.title} by ${audioTrack.info.author} to guild $guildId")
            }

            override fun playlistLoaded(audioPlaylist: AudioPlaylist) {
                if (audioPlaylist.isSearchResult) {
                    trackLoaded(audioPlaylist.tracks[0])
                } else {
                    if (!command.event.guild.selfMember.voiceState.inVoiceChannel()) {
                        if (!command.member.voiceState.inVoiceChannel()) return
                        join(command.member.voiceState.channel)
                    }
                    var time: Long = 0
                    for (i in 0 until audioPlaylist.tracks.size) {
                        time += audioPlaylist.tracks[i].info.length
                        addToQueue(AudioInfo(audioPlaylist.tracks[i], "${sender.name}#${sender.discriminator}", command.event.textChannel.id))
                    }
                    message.editMessage("(${Emote.SUCCESS}): Playlist `${audioPlaylist.name}` with __${audioPlaylist.tracks.size}__ songs has been added to the queue. ${musicBot.musicManager.getPlayTime(time)}").queue()
                    logger.info("Loaded Playlist ${audioPlaylist.name} to guild $guildId")
                }
            }

            override fun noMatches() {
                message.editMessage("(${Emote.ERROR}): No matching song for the query `${if (query.startsWith("ytsearch:")) query.substring(9) else query}` could be found").queue()
                logger.info("No matches found for query: ${if (query.startsWith("ytsearch:")) query.substring(9) else query}")
            }

            override fun loadFailed(e: FriendlyException) {
                message.editMessage("(${Emote.ERROR}): An error occurred while attempting to load the song. Please try again later.").queue()
                logger.error("Error occurred while loading query: ${if (query.startsWith("ytsearch:")) query.substring(9) else query}", e)
            }
        })
    }

}