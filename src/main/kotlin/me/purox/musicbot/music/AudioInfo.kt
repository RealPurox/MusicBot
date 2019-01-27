package me.purox.musicbot.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack

class AudioInfo(val audioTrack: AudioTrack, val requester: String, private val channel: String) {

    var playing = false
    var queuePos : Int = -1

    fun createNew(): AudioInfo {
        return AudioInfo(audioTrack.makeClone(), requester, channel)
    }

    fun isEqualTo(audioInfo: AudioInfo): Boolean {
        return this.audioTrack.info.author == audioInfo.audioTrack.info.author && this.audioTrack.info.title == audioInfo.audioTrack.info.title &&
                this.audioTrack.info.identifier == audioInfo.audioTrack.info.identifier && this.audioTrack.info.length == audioInfo.audioTrack.info.length &&
                this.audioTrack.info.uri == audioInfo.audioTrack.info.uri && audioInfo.requester == this.requester &&
                this.channel == audioInfo.channel &&
                this.audioTrack.identifier == audioInfo.audioTrack.identifier
    }

    override fun toString(): String {
        return "[AudioInfo: Title:${audioTrack.info.title} || URI:${audioTrack.info.uri} || Requester: $requester || Channel:$channel]"
    }
}
