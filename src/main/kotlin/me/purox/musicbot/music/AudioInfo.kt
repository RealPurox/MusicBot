package me.purox.musicbot.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.entities.User

class AudioInfo(val audioTrack: AudioTrack, val requester: User, private val channel: TextChannel) {

    var playing = false

    fun createNew(): AudioInfo {
        return AudioInfo(audioTrack.makeClone(), requester, channel)
    }


    fun isEqualTo(audioInfo: AudioInfo): Boolean {
        return this.audioTrack.info.author == audioInfo.audioTrack.info.author && this.audioTrack.info.title == audioInfo.audioTrack.info.title &&
                this.audioTrack.info.identifier == audioInfo.audioTrack.info.identifier && this.audioTrack.info.length == audioInfo.audioTrack.info.length &&
                this.audioTrack.info.uri == audioInfo.audioTrack.info.uri && audioInfo.requester.idLong == this.requester.idLong &&
                this.channel.id == audioInfo.channel.id &&
                this.audioTrack.identifier == audioInfo.audioTrack.identifier
    }

    override fun toString(): String {
        return "[AudioInfo: Title:${audioTrack.info.title} || URI:${audioTrack.info.uri} || Requester: ${requester.name}#${requester.discriminator} || Channel:$channel]"
    }
}
