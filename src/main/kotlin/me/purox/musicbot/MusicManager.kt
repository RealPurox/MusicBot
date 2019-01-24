package me.purox.musicbot

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import me.purox.musicbot.music.GuildPlayer
import net.dv8tion.jda.core.entities.Guild
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MusicManager {

    private val logger = LoggerFactory.getLogger(MusicManager::class.java)

    val audioPlayerManager: AudioPlayerManager = DefaultAudioPlayerManager()
    val guildPlayers: MutableMap<String, GuildPlayer> = mutableMapOf()
    val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(50)

    init {
        AudioSourceManagers.registerRemoteSources(audioPlayerManager)

        executor.scheduleAtFixedRate({
            for ((id, player)in guildPlayers) {
                if (player.destroyTime <= System.currentTimeMillis()) {
                    player.destroy()
                    logger.info("Destroyed guild player for id=$id because of inactivity")
                }
            }
        }, 2, 2, TimeUnit.MINUTES)
    }

    fun getGuildPlayer(guild: Guild) : GuildPlayer {
        if (guildPlayers[guild.id] == null) {
            logger.info("Created Music Player for guild ${guild.name} (${guild.id})")
            guildPlayers[guild.id] = GuildPlayer(guild, musicBot)
        }
        return guildPlayers[guild.id]!!
    }

    fun getPlayTime(time: Long) : String {
        val totalSecs : Long = time / 1000

        val hours: Long = totalSecs / 3600
        val mins : Long = (totalSecs / 60) % 60
        val secs : Long = totalSecs % 60

        return "(`" + (if (hours == 0L) "" else String.format("%02d", hours) + "`:`") + String.format("%02d", mins) + "`:`" + String.format("%02d", secs) + "`)"
    }

}