package me.purox.musicbot.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import me.purox.musicbot.MusicBot
import me.purox.musicbot.commands.Command
import me.purox.musicbot.musicBot
import net.dv8tion.jda.core.entities.*

class GuildPlayer(guild: Guild, musicBot: MusicBot) : AudioEventAdapter() {

    var destroyTime = System.currentTimeMillis() + 60000 //60 seconds
    private var guildId: String = guild.id
    private val audioPlayer: AudioPlayer = musicBot.musicManager.audioPlayerManager.createPlayer()

    init {
        audioPlayer.addListener(this)
        guild.audioManager.sendingHandler = AudioPlayerSendHandler(audioPlayer)
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

    override fun onTrackStart(player: AudioPlayer?, track: AudioTrack?) {
        destroyTime = System.currentTimeMillis() + track?.info?.length!! + 600000 //60 seconds
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        //todo queue or something
    }

    fun loadSong(query: String, command: Command, sender: Member) {
        musicBot.musicManager.audioPlayerManager.loadItem(query, object : AudioLoadResultHandler {
            override fun trackLoaded(audioTrack: AudioTrack) {
                if (!command.event.guild.selfMember.voiceState.inVoiceChannel()) {
                    if (!sender.voiceState.inVoiceChannel()) return
                    join(sender.voiceState.channel)
                }
                audioPlayer.playTrack(audioTrack)
                command.event.channel.sendMessage(":notes: Now playing: ")
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
                        //todo queue / playlist
                    }
                }
            }

            override fun noMatches() { }

            override fun loadFailed(e: FriendlyException) { }
        })
    }

}