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
import me.purox.musicbot.musicBot
import net.dv8tion.jda.core.entities.*
import java.util.concurrent.LinkedBlockingQueue

class GuildPlayer(guild: Guild, musicBot: MusicBot) : AudioEventAdapter() {

    private var guildId: String = guild.id

    var destroyTime = System.currentTimeMillis() + 60000 //60 seconds
    val audioPlayer: AudioPlayer = musicBot.musicManager.audioPlayerManager.createPlayer()

    val queue : LinkedBlockingQueue<AudioInfo> = LinkedBlockingQueue()

    init {
        audioPlayer.addListener(this)
        guild.audioManager.sendingHandler = AudioPlayerSendHandler(audioPlayer)
    }

    fun addToQueue(audioInfo: AudioInfo) {
        //already in queue
        if (getAudioInfoId(audioInfo) != -1) return
        queue.add(audioInfo)
        if (!queue.peek().playing) {
            audioPlayer.playTrack(queue.peek().audioTrack)
        }
    }

    fun getQueueDuration() : String {
        return musicBot.musicManager.getPlayTime(queue.stream().mapToLong { info -> info.audioTrack.duration }.count())
    }

    fun getAudioInfoId(audioInfo: AudioInfo) : Int {
        for (i in 0 until queue.size) {
            if (queue.toList()[i].isEqualTo(audioInfo))
                return i + 1
        }
        return -1
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

    fun getAudioInfoById(id: Int) : AudioInfo {
        return queue.toList()[id - 1]
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
        }
    }

    override fun onTrackStart(player: AudioPlayer, track: AudioTrack) {
        destroyTime = System.currentTimeMillis() + track.info.length + 600000 //60 seconds
        val audioInfo = getAudioInfo(track)
        audioInfo?.playing = true
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        val endedInfo = queue.poll()
        endedInfo.playing = false
    }

    fun loadSong(query: String, command: Command, sender: Member) {
        musicBot.musicManager.audioPlayerManager.loadItem(query, object : AudioLoadResultHandler {

            override fun trackLoaded(audioTrack: AudioTrack) {
                if (!command.event.guild.selfMember.voiceState.inVoiceChannel()) {
                    if (!sender.voiceState.inVoiceChannel()) return
                    join(sender.voiceState.channel)
                }
                addToQueue(AudioInfo(audioTrack, sender.user, command.event.textChannel))
                command.channel.sendMessage("${Emote.SUCCESS} | `${audioTrack.info.title}` by __${audioTrack.info.author}__ has been added to the queue. ${musicBot.musicManager.getPlayTime(audioTrack.duration)}").queue()
            }

            override fun playlistLoaded(audioPlaylist: AudioPlaylist) {
                if (audioPlaylist.isSearchResult) {
                    trackLoaded(audioPlaylist.tracks[0])
                } else {
                    if (!command.event.guild.selfMember.voiceState.inVoiceChannel()) {
                        if (!sender.voiceState.inVoiceChannel()) return
                        join(sender.voiceState.channel)
                    }
                    var time: Long = 0
                    for (i in 0 until audioPlaylist.tracks.size) {
                        time += audioPlaylist.tracks[i].info.length
                        addToQueue(AudioInfo(audioPlaylist.tracks[i], sender.user, command.event.textChannel))
                    }
                    command.channel.sendMessage("${Emote.SUCCESS} | The playlist `${audioPlaylist.name}` with __${audioPlaylist.tracks.size}__ songs has been added to the queue. ${musicBot.musicManager.getPlayTime(time)}").queue()
                }
            }

            override fun noMatches() {
                command.channel.sendMessage("${Emote.ERROR} | No matching song for the query `${if (query.startsWith("ytsearch:")) query.substring(9) else query}` could be found").queue()
            }

            override fun loadFailed(e: FriendlyException) {
                command.channel.sendMessage("${Emote.ERROR} | An error occurred while attempting to load the song. Please try again later.").queue()
            }
        })
    }

}