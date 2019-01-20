package me.purox.musicbot

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import me.purox.musicbot.music.GuildPlayer
import net.dv8tion.jda.core.entities.Guild
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

open class MusicManager {

    val audioPlayerManager: AudioPlayerManager = DefaultAudioPlayerManager()
    val guildPlayers: MutableMap<String, GuildPlayer> = mutableMapOf()
    val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(50)

    init {
        AudioSourceManagers.registerRemoteSources(audioPlayerManager)

        executor.scheduleAtFixedRate({
            for ((id, player)in guildPlayers) {
                if (player.destroyTime <= System.currentTimeMillis()) {
                    player.destroy()
                    println("Destroyed guild player for id=$id because of inactivity")
                }
            }
        }, 2, 2, TimeUnit.MINUTES)
    }

    open fun getGuildPlayer(guild: Guild) : GuildPlayer? {
        if (guildPlayers[guild.id] == null) {
            println("Created Music Player for guild ${guild.name} (${guild.id})")
            guildPlayers[guild.id] = GuildPlayer(guild)
        }
        return guildPlayers[guild.id]
    }

}