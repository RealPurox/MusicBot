package me.purox.musicbot.listener

import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.events.guild.GuildReadyEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import org.slf4j.LoggerFactory

class ReadyListener : ListenerAdapter() {

    private val logger = LoggerFactory.getLogger(ReadyListener::class.java)

    override fun onReady(event: ReadyEvent) {
        logger.info("Music Bot is ready!")
        logger.info("Logged in as ${event.jda.selfUser.name + "#" + event.jda.selfUser.discriminator} (${event.jda.selfUser.id})")
    }

    override fun onGuildReady(event: GuildReadyEvent) {
        //re-open audio connection if existing
        if (event.guild.selfMember.voiceState.inVoiceChannel())
            event.guild.audioManager.openAudioConnection(event.guild.selfMember.voiceState.channel)
    }
}